package edu.uccs.summers.ui

import scala.swing.Orientation
import scala.swing.BoxPanel
import akka.actor.ActorSystem
import akka.actor.Props
import scala.swing.ListView
import javax.swing.SwingUtilities
import akka.actor.ActorRef
import java.awt.Dimension
import scala.swing.Button
import scala.swing.event.ButtonClicked
import edu.uccs.summers.messages.Forward
import edu.uccs.summers.messages.SimulationCreate
import scala.collection.mutable.ListBuffer
import scala.swing.ScrollPane
import scala.swing.event.SelectionChanged
import edu.uccs.summers.messages.SimulationLookup
import scala.swing.event.MouseClicked

class SimulationListing(simCoordinator : ActorRef) extends BoxPanel(Orientation.Vertical){
  
  preferredSize = new Dimension(600, 800)

  var _simulationClient : ActorRef = null
  val listing = new ListView[String](ListBuffer("Waiting for response")){
    import ListView._
    renderer = Renderer(_.toString())
  }
  listenTo(listing.mouse.clicks)
  contents += new ScrollPane(listing)

  reactions += {
    case MouseClicked(_,_,_,clickCount,_) if clickCount >= 2 => {
      _simulationClient ! Forward(simCoordinator, SimulationLookup(listing.selection.items(0)))
    }
  }
  
  contents += new Button("Start new Simulation"){
    reactions += {
      case ButtonClicked(_) => {
        _simulationClient ! Forward(simCoordinator, SimulationCreate("NEEEW"))
      }
    }
  }
  
  def simulationClient = _simulationClient
  def simulationClient_= (client : ActorRef) = _simulationClient = client
  
  def updateListing(simulations : Set[String]) = {
    SwingUtilities.invokeLater(new Runnable(){
      def run() = {
        println("Updating client listing")
        listing.listData = simulations.toSeq
        repaint
      }
    })
  }
}