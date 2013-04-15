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
import edu.uccs.summers.data.behaviors.BehaviorsParser
import edu.uccs.summers.data.behaviors.RandomWalk
import edu.uccs.summers.data.behaviors.Idle
import edu.uccs.summers.data.behaviors.MoveDirect
import com.typesafe.config.ConfigFactory
import java.awt.Rectangle
import edu.uccs.summers.actors.SimulationMaster
import scala.swing.TabbedPane
import scala.swing._
import edu.uccs.summers.ui.AreaTabPane
import edu.uccs.summers.messages.AddSimulationListener
import edu.uccs.summers.ui.AreaTabPane
import edu.uccs.summers.ui.AreaTabPaneSimulationListener
import edu.uccs.summers.ui.AreaTabPane
import edu.uccs.summers.ui.AreaTabPane
import edu.uccs.summers.messages.SimulationStepRequest
import edu.uccs.summers.messages.SimulationInitialize
import edu.uccs.summers.ui.ControlPanel

object Main extends SimpleSwingApplication{
  
  val base = "test/eg1/"
  
  val config = ConfigFactory.load()
  val system = ActorSystem("DistEg") //, config.getConfig("DistEg").withFallback(config))
  val simulationMaster = system.actorOf(Props(new SimulationMaster))
  
  val tabbedPane = new AreaTabPane(system)
  simulationMaster ! AddSimulationListener(tabbedPane.simulationListener)
  
  lazy val areaPanes = tabbedPane
  lazy val controlPanel = new ControlPanel(system, simulationMaster, tabbedPane)
  
  lazy val mainUi : Panel = new BoxPanel(Orientation.Vertical){ 
    contents += areaPanes
    contents += controlPanel
  }
  
  def top = new MainFrame {
    title = "DistEg"
    contents = mainUi;
  }
}