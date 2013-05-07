package edu.uccs.summers.actors

import akka.actor.Actor
import edu.uccs.summers.messages.SimulationStepPartialResult
import scala.collection._
import edu.uccs.summers.data.Person
import edu.uccs.summers.messages.SimulationStepExecutionComplete
import edu.uccs.summers.data.dto.geometry.Area
import edu.uccs.summers.data.dto.geometry.Geometry
import edu.uccs.summers.data.dto.geometry.TransitionEvent

class SimulationStepAggregator(resultSize : Int) extends Actor {
  
  private var areas : mutable.ListBuffer[Area] = mutable.ListBuffer()
  private var resultCount = 0
  private val latestTransitioners : mutable.Set[TransitionEvent] = mutable.Set()
  
  def receive = {
    case SimulationStepPartialResult(area, transitioners) => {
      resultCount += 1
      areas += area
      latestTransitioners ++= transitioners
      if(resultCount == resultSize){
        
        val newGeometry = new Geometry(areas.toList)
        context.parent ! SimulationStepExecutionComplete(newGeometry, latestTransitioners.toSet)

        resultCount = 0
        areas.clear
        latestTransitioners.clear
      }
    }
  }
}