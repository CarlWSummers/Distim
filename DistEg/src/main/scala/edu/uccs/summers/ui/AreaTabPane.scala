package edu.uccs.summers.ui

import scala.collection.mutable
import scala.swing.TabbedPane
import scala.swing.TabbedPane.Page
import akka.actor.Actor
import edu.uccs.summers.messages.SimulationStepResult
import scala.swing.Panel
import scala.swing.BoxPanel
import scala.swing.Orientation
import edu.uccs.summers.data.dto.geometry.Geometry
import akka.actor.ActorSystem
import akka.actor.Props
import java.awt.Dimension
import edu.uccs.summers.data.dto.population.Person
import edu.uccs.summers.data.dto.geometry.Area
import edu.uccs.summers.messages.SimulationClear
import scala.swing.Swing
import javax.swing.SwingUtilities
import akka.actor.ActorContext

class AreaTabPane(val actorSystem : ActorContext) extends BoxPanel(Orientation.Vertical) {
  import TabbedPane._

  val tabbedPane = new TabbedPane
  contents += tabbedPane
  preferredSize = new Dimension(600, 800)
  val simulationListener = actorSystem.actorOf(Props(new AreaTabPaneSimulationListener(this)))
  
  val areaToPageMap = mutable.Map[String, AreaCanvas]()

  def update(geometry : Geometry){
    for(area <- geometry.areas){
      if(!areaToPageMap.contains(area.name)){
        val canvas = new AreaCanvas
        areaToPageMap += area.name -> canvas
        tabbedPane.pages += new Page(area.name, canvas)
      }
      SwingUtilities.invokeLater(new Runnable{
        def run() {
          areaToPageMap.get(area.name).get.update(area)
        }
      })
    }
  }
  
  def reset() {
    areaToPageMap.values.foreach(_.reset)
  }
  
  def clear() {
    tabbedPane.pages.clear
    areaToPageMap.clear
  }
}

class AreaTabPaneSimulationListener(parent : AreaTabPane) extends Actor {

  def receive = {
    case SimulationStepResult(geometry) => {
      println("Received Update")
      parent.update(geometry)
    }
    
    case SimulationClear => {
      parent.clear()
    }
  }
}