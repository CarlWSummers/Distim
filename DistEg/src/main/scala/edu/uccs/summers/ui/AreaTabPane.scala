package edu.uccs.summers.ui

import scala.collection.mutable
import scala.swing.TabbedPane
import scala.swing.TabbedPane.Page
import akka.actor.Actor
import edu.uccs.summers.messages.SimulationStepResult
import scala.swing.Panel
import scala.swing.BoxPanel
import scala.swing.Orientation
import edu.uccs.summers.data.Geometry
import akka.actor.ActorSystem
import akka.actor.Props
import java.awt.Dimension

class AreaTabPane(val actorSystem : ActorSystem) extends BoxPanel(Orientation.Vertical) {
  import TabbedPane._

  val tabbedPane = new TabbedPane
  contents += tabbedPane
  preferredSize = new Dimension(600, 800)
  val simulationListener = actorSystem.actorOf(Props[AreaTabPaneSimulationListener])
  simulationListener ! this
  
  val areaToPageMap = mutable.Map[String, AreaCanvas]()

  def update(geometry : Geometry){
    for(area <- geometry.areas){
      if(!areaToPageMap.contains(area.name)){
        val canvas = new AreaCanvas
        areaToPageMap += area.name -> canvas
        tabbedPane.pages += new Page(area.name, canvas)
      }
      areaToPageMap.get(area.name).get.update(area)
    }
  }
  
  def reset() {
    areaToPageMap.values.foreach(_.reset)
  }
}

class AreaTabPaneSimulationListener() extends Actor {
  private var parent : AreaTabPane = null
  
  def receive = {
    case SimulationStepResult(geometry) => {
      parent.update(geometry)
    }
    case parent : AreaTabPane => {
      this.parent = parent
    }
  }
}