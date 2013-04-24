package edu.uccs.summers.actors

import akka.actor.Actor
import edu.uccs.summers.messages.SimulationStepPartialResult
import scala.collection._
import edu.uccs.summers.data.Person
import edu.uccs.summers.messages.SimulationStepExecutionComplete
import edu.uccs.summers.data.geometry.Area
import edu.uccs.summers.data.geometry.Geometry

class SimulationStepAggregator(resultSize : Int) extends Actor {
  
  private var areas : mutable.ListBuffer[Area] = mutable.ListBuffer()
  private var resultCount = 0;
  
  def receive = {
    case SimulationStepPartialResult(area) => {
      resultCount += 1;
      areas += area
      if(resultCount == resultSize){
        val newGeometry = Geometry(areas.toList)
        resultCount = 0
        areas.clear
        context.parent ! SimulationStepExecutionComplete(newGeometry)
      }
    }
  }
}