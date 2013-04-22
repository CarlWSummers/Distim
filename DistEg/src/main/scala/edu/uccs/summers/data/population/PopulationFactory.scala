package edu.uccs.summers.data.population

import edu.uccs.summers.data.Person
import edu.uccs.summers.data.geometry.Area
import edu.uccs.summers.data.behaviors.Behavior
import scala.util.Random
import edu.uccs.summers.data.geometry.shapes.Vec2d

object PopulationFactory {
  val rnd = Random
  
  def createPerson(id : String, descriptor : PopulationArchetypeDescriptor, area : Area) : Person = {
//    val person = Person(id, null, PhysicalProperties(area.generateSpawnPoint, Vec2d(rnd.nextInt(10), rnd.nextInt(10)), 30, 1), area)
    val person = Person(id, null, PhysicalProperties(area.generateSpawnPoint, Vec2d(5,0), 30, 1), area)
    person.executor = descriptor.behavior.executor(person)
    return person
  }
}