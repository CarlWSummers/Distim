package edu.uccs.summers.actors

import scala.collection._
import akka.actor.Actor
import edu.uccs.summers.messages.Compute
import edu.uccs.summers.data.Person
import edu.uccs.summers.messages.SimulationStepPartialResult
import edu.uccs.summers.data.Person
import edu.uccs.summers.data.geometry.shapes.Vec2d
import edu.uccs.summers.data.population.PhysicalProperties
import edu.uccs.summers.messages.Compute

class SimulationStepExecutor extends Actor{
  
  def receive = {
    case Compute(area, responseDest) => {
      area.update()
//      val updatedPop = pop.map(person => {
//        val currentPos = person.dynamics.position
//        val updatedPerson = person.update(area, pop)
//        val currentVel = person.dynamics.velocity
//        Person(person.id, person.executor, dynamics, area)
//      })
      responseDest ! SimulationStepPartialResult(area)
    }
  }
}