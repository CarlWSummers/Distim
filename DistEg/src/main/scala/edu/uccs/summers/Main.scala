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
import edu.uccs.summers.actors.SimulationMaster
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
import edu.uccs.summers.messages.AddSimulationListener
import akka.actor.{ ActorRef, Props, Actor, ActorSystem, Cancellable }
import scala.concurrent.duration._
import edu.uccs.summers.messages.Forward
import edu.uccs.summers.messages.SimulationListing
import javax.swing.JOptionPane
import edu.uccs.summers.ui.SimulationListing
import edu.uccs.summers.messages.SimulationListingReponse
import edu.uccs.summers.messages.SimulationReference
import javax.swing.SwingUtilities

object Main extends SimpleSwingApplication{
  
  val system = ActorSystem("DistEg", ConfigFactory.load().getConfig("remotelookup"))
  val host = JOptionPane.showInputDialog("Remote IP", "192.168.0.104")
  val port = JOptionPane.showInputDialog("Remote Port", "13552")
  val simCoordinator = system.actorFor("akka://SimulationCoordination@" + host + ":" + port+ "/user/coordinator")
  
  lazy val mainUi : BoxPanel = new BoxPanel(Orientation.Vertical){ 
    contents += simulationListingPanel
  }

  val simulationListingPanel = new SimulationListing(simCoordinator)
  val simClient = system.actorOf(Props(new SimulationClient(simulationListingPanel, mainUi)))
  simulationListingPanel.simulationClient = simClient
  simClient ! Forward(simCoordinator, SimulationListing)
  
  def top = new MainFrame {
    title = "Distributed Egress"
    contents = mainUi;
  }
}

class SimulationClient(listingPanel : SimulationListing, mainUI : BoxPanel) extends Actor {
  def receive = {
    case Forward(dest, message) => {
      dest ! message
    }
    
    case SimulationListingReponse(values) => {
      listingPanel.updateListing(values)
    }
    
    case SimulationReference(simMaster) => {
      println("Received Reference");
      SwingUtilities.invokeLater(new Runnable(){
        def run() {
          val tabbedPane = new AreaTabPane(context)
          simMaster ! AddSimulationListener(tabbedPane.simulationListener)
          val areaPanes = tabbedPane
          val controlPanel = new ControlPanel(context, simMaster, tabbedPane)
          mainUI.contents.clear
          mainUI.contents += areaPanes
          mainUI.contents += controlPanel
          mainUI.repaint
        }
      })
    }
  }
}