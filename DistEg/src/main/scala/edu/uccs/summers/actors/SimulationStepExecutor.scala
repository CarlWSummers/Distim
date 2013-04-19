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
        var currentPos = person.position
        val updatedPerson = person.update(area, pop)
        var currentVel = person.velocity
        area.objects.foreach(t => {
          if(t.isCollidable){
            val collision = t.resolveCollision(currentPos, currentVel)
            collision.foreach( p => {
              currentPos = p._1
              currentVel = p._2
              Person(person.id, person.executor, p._1, p._2, area)
            })
          }
        })
        Person(person.id, person.executor, currentPos + currentVel, currentVel, area)
      })
      responseDest ! SimulationStepPartialResult(updatedPop)
    }
  }
}