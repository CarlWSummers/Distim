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
import edu.uccs.summers.data.Person
import edu.uccs.summers.data.geometry.Area
import edu.uccs.summers.messages.SimulationClear
import scala.swing.Swing
import javax.swing.SwingUtilities

class AreaTabPane(val actorSystem : ActorSystem) extends BoxPanel(Orientation.Vertical) {
  import TabbedPane._

  val tabbedPane = new TabbedPane
  contents += tabbedPane
  preferredSize = new Dimension(600, 800)
  val simulationListener = actorSystem.actorOf(Props[AreaTabPaneSimulationListener])
  simulationListener ! this
  
  val areaToPageMap = mutable.Map[String, AreaCanvas]()

  def update(geometry : Geometry, pop : Set[Person]){
    val popByArea = pop.foldLeft(mutable.Map[Area, mutable.Set[Person]]())((areaToPeopleMap, person) => {
      if(!areaToPeopleMap.contains(person.currentArea)){
        areaToPeopleMap.put(person.currentArea, mutable.Set())
      }
      areaToPeopleMap(person.currentArea) += person
      areaToPeopleMap
    })
    
    for(area <- geometry.areas){
      if(!areaToPageMap.contains(area.name)){
        val canvas = new AreaCanvas
        areaToPageMap += area.name -> canvas
        tabbedPane.pages += new Page(area.name, canvas)
      }
      SwingUtilities.invokeLater(new Runnable{
        def run() {
          areaToPageMap.get(area.name).get.update(area, popByArea(area).toSet)
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

class AreaTabPaneSimulationListener() extends Actor {
  private var parent : AreaTabPane = null
  
  def receive = {
    case SimulationStepResult(geometry, pop) => {
      parent.update(geometry, pop)
    }
    
    case SimulationClear => {
      parent.clear()
    }
    
    case parent : AreaTabPane => {
      this.parent = parent
    }
  }
}