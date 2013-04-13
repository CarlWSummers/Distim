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
import edu.uccs.summers.actors.SimulationMaster

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
      exit
  } 
  
  val topo = new Topography(base + "topography.txt")
  val geometry = new Geometry(base + "geometry.txt")
  val popActor = system.actorOf(Props(new Population(base + "population.json", topo, behaviors toMap)), name = "master")
  
  val simulationMaster = system.actorOf(Props(new SimulationMaster))
  
  var pop = null : Population
  var tickCount = 0
  
  lazy val ui = new Panel {
    var drawMouseCoords = false
    var drawPersonLabel = true;
    var mouseX = 0;
    var mouseY = 0;
    var translateX = 0.0;
    var translateY = 0.0;
    
    var scaleFactor = 1.0;
    
    focusable = true
    opaque = true
    preferredSize = new Dimension(800, 800)
    
    override def paintComponent(g : Graphics2D) = {
      val area = geometry.areas.head
      background = area.bgColor
      super.paintComponent(g);
      g.translate(translateX, translateY)
      g.scale(scaleFactor, scaleFactor);
      area.draw(g);
    }
    
    listenTo(mouse.clicks, mouse.moves, mouse.wheel)
    reactions += {
      case MouseEntered(_,_,_) => drawMouseCoords = true; 
      case MouseExited(_,_,_) => drawMouseCoords = false
      case MouseMoved(_,p,_) => {
        mouseX = p.getX.intValue
        mouseY = p.getY.intValue
      }
      case MouseDragged(_,p,_) => {
        translateX += (p.getX() - mouseX)
        translateY += (p.getY() - mouseY)
        mouseX = p.getX.intValue
        mouseY = p.getY.intValue
        repaint
      }
      case MouseWheelMoved(_, _, _, direction) => {
        if(direction < 0) scaleFactor /= 1.125
        else scaleFactor *= 1.125
        repaint
      }
    }
    
    def reset() = {
      translateX = 0
      translateY = 0
      repaint
    }
    
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
            tickBtn.enabled = false
            text = "Pause"
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
          }
        }
      }
      
      contents += tickBtn
    }
    
    contents += new BoxPanel(Orientation.Horizontal){
      contents += new Button("Reset") {
        reactions += { case ButtonClicked(_) => ui.reset }
      }
      
      contents += new Button("Toggle Labels") {
        reactions += { case ButtonClicked(_) => ui.drawPersonLabel = !ui.drawPersonLabel}
      }
    }
  }
  
  def top = new MainFrame {
    title = "DistEg"
    contents = mainUi;
  }
}