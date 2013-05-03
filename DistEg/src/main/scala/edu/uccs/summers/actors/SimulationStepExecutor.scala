package edu.uccs.summers.actors

import scala.collection._
import akka.actor.Actor
import edu.uccs.summers.messages.Compute
import edu.uccs.summers.data.Person
import edu.uccs.summers.messages.SimulationStepPartialResult
import edu.uccs.summers.data.Person
import edu.uccs.summers.data.population.PhysicalProperties
import edu.uccs.summers.messages.Compute
import edu.uccs.summers.data.geometry.Area

class SimulationStepExecutor(val area : Area) extends Actor{
  area.initialize()
  
  def receive = {
    case Compute(responseDest) => {
      area.update
      println("Sending Partial Result for area : " + area.name)
      responseDest ! SimulationStepPartialResult(area.translate)
      println("Finished sending Partial Result for area : " + area.name)
    }
  }
}