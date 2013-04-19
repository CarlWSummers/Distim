package edu.uccs.summers.ui

import akka.actor.ActorRef
import scala.swing.BoxPanel
import scala.swing.Button
import scala.swing.Orientation
import scala.swing.Slider
import scala.swing.event.ButtonClicked
import scala.swing.event.ValueChanged
import scala.swing.FileChooser
import akka.actor.ActorRef
import akka.actor.ActorSystem
import akka.actor.Props
import akka.actor.Actor
import java.io.File
import edu.uccs.summers.data.SimulationInitData
import edu.uccs.summers.messages.SimulationInitialize
import edu.uccs.summers.messages.SimulationStepRequest
import edu.uccs.summers.messages.SimulationStepResult
import javax.swing.JOptionPane
import edu.uccs.summers.messages.InitSuccessful
import edu.uccs.summers.messages.InitFailed
import edu.uccs.summers.messages.Forward
import edu.uccs.summers.messages.SimulationSpeed
import scala.swing.event.ButtonClicked

class ControlPanel(actorSystem : ActorSystem, simMaster : ActorRef, areaTabPanel : AreaTabPane) extends BoxPanel(Orientation.Vertical){

  val simulationListener = actorSystem.actorOf(Props[ControlPanelSimulationListener])
  simulationListener ! this
  
  private val tickBtn = new Button("Tick") {
    listenTo(this)
    
    reactions += {
      case ButtonClicked(_) => {
        simMaster ! SimulationStepRequest
      }
    }
  }
  
  private val runBtn = new Button("Run") {
    listenTo(this)
    reactions += {
      case ButtonClicked(_) =>
        tickBtn.enabled = false
        text = "Pause"
    }
  }
  
  private val loadBtn = new Button("Load") {
    listenTo(this)
    reactions += {
      case ButtonClicked(_) => {
        val chooser = new FileChooser(new File("."))
        chooser.title = "Load Simulation"
        try{
          chooser.showOpenDialog(null) match {
            case FileChooser.Result.Approve => {
              simulationListener ! Forward(simMaster, SimulationInitialize(
                new SimulationInitData(chooser.selectedFile)))
            }
          }
        }catch{
          case e : Exception => {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Error loading Simulation", JOptionPane.ERROR_MESSAGE);
          }
        }
      }
    }
  }
  
  private val speedSlider = new Slider {
    listenTo(this)
    max = 3010
    min = 10
    value = 2710
    majorTickSpacing = 100
    paintTicks = true
    snapToTicks = true
    
    reactions += {
      case ValueChanged(_) if !adjusting => {
        simulationListener ! Forward(simMaster, SimulationSpeed(value))
      }
    }
  }
  
  contents += new BoxPanel(Orientation.Horizontal){
    contents += loadBtn
    contents += runBtn
    contents += speedSlider
    contents += tickBtn
  }
    
  contents += new BoxPanel(Orientation.Horizontal){
    contents += new Button("Reset") {
      reactions += {
        case ButtonClicked(_) => areaTabPanel.reset();
      }
    }
    contents += new Button("Toggle Labels") {
    }
  }
}

class ControlPanelSimulationListener() extends Actor {
  private var parent : ControlPanel = null
  
  def receive = {
    case parent : ControlPanel => {
      this.parent = parent
    }
    case InitSuccessful => {
      println("SUCCESS")
    }
    case InitFailed(e) => {
      println(e)
    }
    case Forward(dest, message) => {
      dest ! message
    }
  }
}