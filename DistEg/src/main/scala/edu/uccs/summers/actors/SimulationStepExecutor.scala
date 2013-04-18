package edu.uccs.summers.actors
import scala.collection._
import akka.actor.Actor
import edu.uccs.summers.messages.Compute
import edu.uccs.summers.data.Person
import edu.uccs.summers.messages.SimulationStepPartialResult
import edu.uccs.summers.data.Person
import edu.uccs.summers.data.geometry.shapes.Point

class SimulationStepExecutor extends Actor{
  
  def receive = {
    case Compute(area, pop, responseDest) => {
      val updatedPop = pop.map(person => {
        val updatedPerson = person.update(area, pop)
        Person(person.id, person.executor, Point(person.position.x + person.velocity.x, person.position.y + person.velocity.y), updatedPerson.velocity, area)
      })
      responseDest ! SimulationStepPartialResult(updatedPop)
    }
  }
}