package edu.uccs.summers

import scala.swing._
import scala.swing.BoxPanel
import scala.swing.Label
import scala.swing.MainFrame
import scala.swing.Orientation
import scala.swing.Panel
import scala.swing.SimpleSwingApplication
import scala.swing.Slider
import com.typesafe.config.ConfigFactory
import akka.actor.ActorSystem
import akka.actor.Cancellable
import edu.uccs.summers.actors.SimulationMaster
import edu.uccs.summers.data.Topography
import edu.uccs.summers.data.behaviors.Action
import edu.uccs.summers.data.behaviors.Behavior
import edu.uccs.summers.data.behaviors.BehaviorsParser
import edu.uccs.summers.data.behaviors.MoveDirect
import edu.uccs.summers.data.behaviors.ParsingContext
import edu.uccs.summers.data.behaviors.RandomWalk
import edu.uccs.summers.ui.AreaTabPane
import edu.uccs.summers.ui.AreaTabPane
import edu.uccs.summers.ui.AreaTabPane
import edu.uccs.summers.ui.AreaTabPane
import edu.uccs.summers.ui.ControlPanel
import akka.actor.Props
import edu.uccs.summers.messages.AddSimulationListener

object Main extends SimpleSwingApplication{
  
  val base = "test/eg1/"
  
  val config = ConfigFactory.load()
  val system = ActorSystem("DistEg") //, config.getConfig("DistEg").withFallback(config))
  val simulationMaster = system.actorOf(Props(new SimulationMaster(system)))
  
  val tabbedPane = new AreaTabPane(system)
  simulationMaster ! AddSimulationListener(tabbedPane.simulationListener)
  
  lazy val areaPanes = tabbedPane
  lazy val controlPanel = new ControlPanel(system, simulationMaster, tabbedPane)
  
  lazy val mainUi : Panel = new BoxPanel(Orientation.Vertical){ 
    contents += areaPanes
    contents += controlPanel
  }
  
  def top = new MainFrame {
    title = "Distributed Egress"
    contents = mainUi;
  }
}