package edu.uccs.summers.actors

import akka.actor.Actor
import edu.uccs.summers.messages.SimulationStepPartialResult
import scala.collection._
import edu.uccs.summers.data.Person
import edu.uccs.summers.messages.SimulationStepExecutionComplete

class SimulationStepAggregator(resultSize : Int) extends Actor {
  
  private var population : mutable.Set[Person] = mutable.Set()
  private var resultCount = 0;
  
  def receive = {
    case SimulationStepPartialResult(partialPop) => {
      
      resultCount += 1;
      population ++= partialPop
      if(resultCount == resultSize){
        val result = population.toSet
        resultCount = 0
        population = mutable.Set()
        context.parent ! SimulationStepExecutionComplete(result)
      }
    }
  }
}