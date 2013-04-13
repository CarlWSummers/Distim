package edu.uccs.summers

import java.awt.Color
import scala.swing.Swing._
import scala.swing.{MainFrame, Panel}
import scala.swing.event._
import java.awt.{Color, Graphics2D, Point, geom}
import scala.swing.MainFrame
import scala.swing.Panel
import scala.swing.SimpleSwingApplication
import scala.swing.event.MouseWheelMoved$
import scala.io.Source
import edu.uccs.summers.data.Topography
import edu.uccs.summers.data.Population
import java.awt.Dimension
import edu.uccs.summers.data._
import akka.actor.Actor
import akka.actor.ActorSystem
import akka.actor.Props
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit
import edu.uccs.summers.messages.PopulationRequest
import edu.uccs.summers.messages.PopulationResponse
import akka.dispatch.ExecutionContexts
import scala.concurrent.duration._
import edu.uccs.summers.data.behaviors.ParsingContext
import edu.uccs.summers.data.behaviors.Action
import edu.uccs.summers.data.behaviors.Behavior
import scala.collection.mutable.Map
import scala.swing.BoxPanel
import scala.swing.Orientation
import scala.swing.Label
import scala.swing.Button
import scala.swing.Slider
import akka.actor.Cancellable
import java.awt.Font
import java.awt.RenderingHints
import edu.uccs.summers.data.behaviors.interpreter.BehaviorsParser
import edu.uccs.summers.data.behaviors.RandomWalk
import edu.uccs.summers.data.behaviors.Idle
import edu.uccs.summers.data.behaviors.MoveDirect
import com.typesafe.config.ConfigFactory
import java.awt.Rectangle

object Main extends SimpleSwingApplication{
  
  val base = "test/eg1/"
  
  val config = ConfigFactory.load()
  val system = ActorSystem("DistEg") //, config.getConfig("DistEg").withFallback(config))
  val driver : ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
  
  var behaviors = Map[String, Behavior]()
  val parser = new BehaviorsParser(new ParsingContext)
  parser.bind("RandomWalk", new RandomWalk)
  parser.bind("Idle", new Idle)
  parser.bind("MoveDirect", new MoveDirect)
  
  parser.parseAll(parser.behaviorListing, Source.fromFile(base + "Behaviors.txt").bufferedReader) match {
    case parser.Success(r, t) => {
      r.foreach(b => behaviors += (b.name -> b))
    }
    case e => 
      println("Failed to parse Behaviors File:" + e)
      sys.exit
  } 
  
  val topo = new Topography(base + "topography.txt")
  val popActor = system.actorOf(Props(new Population(base + "population.json", topo, behaviors toMap)), name = "master")
  var pop = null : Population
  var tickCount = 0
  
  lazy val ui = new Panel {
    private var _gridSize = 10
    private var minGridSize = 2;
    private var maxGridSize = 22;
    
    var drawMouseCoords = false
    var drawPersonLabel = true;
    var mouseX = 0;
    var mouseY = 0;
    var translateX = 0.0;
    var translateY = 0.0;
    
    focusable = true
    opaque = true

    preferredSize = new Dimension(800, 800)
    background = new Color(25, 99, 40)
    override def paintComponent(g : Graphics2D) = {
      super.paintComponent(g)
      g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      g.setFont(new Font("Serif", Font.PLAIN, 12));
      g.setColor(Color.LIGHT_GRAY)
      g.drawString("Tick Count: " + tickCount, 0, size.height - 14)
      g.drawString("Pop Count : " + (if (pop != null) pop.size else "-"), 0, size.height - 2)

      font = new Font("Serif", Font.PLAIN, 24 - (maxGridSize - _gridSize));
      g.setFont(font);
      
      g.translate(translateX, translateY)
      
      val maxDim = topo.maxDimension
      for(i <- 0 to maxDim){
        for(j <- 0 to maxDim){
          topo.getType(i,j) match {
            case _:Exit =>
              g.setColor(Color.YELLOW.darker)
            case _:Wall =>
              g.setColor(Color.BLACK)
            case _:Open =>
              g.setColor(background)
            case _ =>
              g.setColor(Color.RED)
          }
          g.fillRect(j*_gridSize, i*_gridSize, _gridSize, _gridSize);
        }
      }
      if(pop != null){
        for(p <- pop.map.values){
//          val personInSwingCoords = convertFromGridToSwing(p.position.x, p.position.y);
//          val rect = new Rectangle(personInSwingCoords._1, personInSwingCoords._2, _gridSize, _gridSize)
//          val mouseInGridCoords = convertFromSwingToGrid(mouseY, mouseX)
//          if(drawPersonLabel || rect.contains(mouseInGridCoords._1, mouseInGridCoords._2)){
          if(drawPersonLabel){
            g.setColor(Color.WHITE)
            g.drawString(
              p.name, 
              p.position.y * _gridSize + (1.5 * _gridSize).toInt, 
              p.position.x * _gridSize + _gridSize)
            g.drawString(
              p.executor.behavior.name + " : " + p.executor.currentState.name, 
              p.position.y * _gridSize + (1.5 * _gridSize).toInt, 
              p.position.x * _gridSize + 2*_gridSize)
          }
          g.setColor(Color.BLUE)
          g.fillRect(
            p.position.y *_gridSize, 
            p.position.x *_gridSize, 
            _gridSize, _gridSize);
        }
      }
      if(drawMouseCoords){
        g.setColor(Color.WHITE)
//        g.drawString(
//          "%d,%d".format(
//            (mouseY - translateY.intValue) / _gridSize, 
//            (mouseX  - translateX.intValue) / _gridSize), 
//          mouseX - translateX.intValue(), 
//          mouseY - translateY.intValue)
//        g.drawString(
//          "%d".format(topo.distance(
//            (mouseY - translateY.intValue) / _gridSize, 
//            (mouseX  - translateX.intValue) / _gridSize)), 
//            mouseX - translateX.intValue(), 
//            mouseY - translateY.intValue)
      }
    }
    def convertFromSwingToGrid(x : Int, y : Int) = 
      ((x  - translateX.intValue) / _gridSize, (y - translateY.intValue) / _gridSize)
    
    def convertFromGridToSwing(x : Int, y : Int) = 
      (x * _gridSize + translateX.intValue, y * _gridSize + translateY.intValue)
    
    
    listenTo(mouse.clicks, mouse.moves, mouse.wheel)
    reactions += {
      case MouseEntered(_,_,_) => drawMouseCoords = true; 
      case MouseExited(_,_,_) => drawMouseCoords = false
      case MouseMoved(_,p,_) => {
        mouseX = p.getX.intValue
        mouseY = p.getY.intValue
//        repaint
      }
      case MouseDragged(_,p,_) => {
        translateX += (p.getX() - mouseX)//ui.size.width / 2)
        translateY += (p.getY() - mouseY)//ui.size.height / 2)
        mouseX = p.getX.intValue
        mouseY = p.getY.intValue
        repaint
      }
      case MouseWheelMoved(source, point, modifiers, value) => {
        gridSize = gridSize + value
      }
    }
    
    def reset() = {
      translateX = 0
      translateY = 0
      _gridSize = 10
      repaint
    }
    
    def gridSize = _gridSize
    def gridSize_= (value : Int):Unit = {
      if(value >= minGridSize && value <= maxGridSize){
        _gridSize = value
        repaint
      }
    }
  }

  val Tick = "tick"
  val tickActor = system.actorOf(Props(new Actor {
    def receive = {
      case Tick => popActor ! PopulationRequest
      case e : PopulationResponse => {
        tickCount += 1
        pop = e.pop
        ui.repaint
      }
    }
  }))
  
  var delay = 300
  var future : Option[Cancellable] = None //scheduleTick(delay)
    
  def scheduleTick(delay : Int = 100) = {
    Some(system.scheduler.schedule(
      Duration.Zero,
      delay milliseconds,
      tickActor,
      Tick)(ExecutionContexts.global))
  }
  
  lazy val mainUi : Panel = new BoxPanel(Orientation.Vertical) {
    contents += ui;
    
    private val tickBtn = new Button("Tick") {
      listenTo(this)
      
      reactions += {
        case ButtonClicked(_) =>
          popActor ! PopulationRequest
          tickCount += 1
      }
    }
    
    contents += new BoxPanel(Orientation.Horizontal){
      contents += new Button("Run"){
        listenTo(this)
        reactions += {
          case ButtonClicked(_) =>
            if(!future.isDefined || future.get.isCancelled){
              future = scheduleTick(delay)
              tickBtn.enabled = false
              text = "Pause"
            }else{
              future.get.cancel
              tickBtn.enabled = true
              text = "Run"
            }
        }
      }
      contents += new Slider {
        listenTo(this)
        max = 3010
        min = 10
        value = 2710
        majorTickSpacing = 100
        paintTicks = true
        snapToTicks = true
        
        reactions += {
          case ValueChanged(_) if !adjusting => {
            delay = max + min - value
            if(!future.get.isCancelled){
              future.get.cancel
              future = scheduleTick(delay)
            }
          }
        }
      }
      
      contents += tickBtn
    }
    
    contents += new Button("Reset") {
      reactions += { case ButtonClicked(_) => ui.reset }
    }
  }
  
  def top = new MainFrame {
    title = "DistEg"
    contents = mainUi;
  }
}