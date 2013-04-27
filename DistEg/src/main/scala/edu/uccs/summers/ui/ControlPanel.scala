package edu.uccs.summers.ui

import java.io.File
import java.util.prefs.Preferences
import scala.concurrent.duration.DurationInt
import scala.swing.BoxPanel
import scala.swing.Button
import scala.swing.FileChooser
import scala.swing.Orientation
import scala.swing.Slider
import scala.swing.event.ButtonClicked
import scala.swing.event.ValueChanged
import akka.actor.Actor
import akka.actor.ActorContext
import akka.actor.ActorRef
import akka.actor.Props
import akka.actor.actorRef2Scala
import edu.uccs.summers.data.SimulationInitData
import edu.uccs.summers.messages.Forward
import edu.uccs.summers.messages.InitFailed
import edu.uccs.summers.messages.InitSuccessful
import edu.uccs.summers.messages.SimulationInitialize
import edu.uccs.summers.messages.SimulationSpeed
import edu.uccs.summers.messages.SimulationStart
import edu.uccs.summers.messages.SimulationStepRequest
import edu.uccs.summers.messages.SimulationStop
import javax.swing.JOptionPane
import edu.uccs.summers.data.SimulationInitDataParser

class ControlPanel(actorSystem : ActorContext, simMaster : ActorRef, areaTabPanel : AreaTabPane) extends BoxPanel(Orientation.Vertical){

  val simulationListener = actorSystem.actorOf(Props(new ControlPanelSimulationListener(this)))
  
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
        text match {
          case "Run" =>
            tickBtn.enabled = false
            text = "Pause"
            simMaster ! SimulationStart
            
          case "Pause" =>
            tickBtn.enabled = true
            text = "Run"
            simMaster ! SimulationStop
            
        }
    }
  }
  
  private val loadBtn = new Button("Load") {
    listenTo(this)
    reactions += {
      case ButtonClicked(_) => {
        val chooser = new FileChooser(new File(Preferences.userRoot().node("DistEg").get("lastFile", ".")))
        chooser.title = "Load Simulation"
        try{
          chooser.showOpenDialog(null) match {
            case FileChooser.Result.Approve => {
              simulationListener ! Forward(simMaster, SimulationInitialize(
                new SimulationInitData(new SimulationInitDataParser().parse(chooser.selectedFile))))
              Preferences.userRoot().node("DistEg").put("lastFile", chooser.selectedFile.getCanonicalPath())
            }
            case _ => {} 
          }
        }catch{
          case e : Exception => {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Error loading Simulation", JOptionPane.ERROR_MESSAGE)
          }
        }
      }
    }
  }
  
  private val speedSlider = new Slider {
    listenTo(this)
    max = 1510
    min = 10
    value = 1310
    majorTickSpacing = 100
    minorTickSpacing = 10
    paintTicks = true
    snapToTicks = true
    
    reactions += {
      case ValueChanged(_) if !adjusting => {
        simulationListener ! Forward(simMaster, SimulationSpeed((max - value + min) milliseconds))
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
        case ButtonClicked(_) => areaTabPanel.reset()
      }
    }
    contents += new Button("Toggle Labels") {
    }
  }
}

class ControlPanelSimulationListener(parent : ControlPanel) extends Actor {
  
  def receive = {
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