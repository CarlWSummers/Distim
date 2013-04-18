package edu.uccs.summers.data.population

import edu.uccs.summers.data.Person
import edu.uccs.summers.data.geometry.Area
import edu.uccs.summers.data.behaviors.Behavior
import scala.util.Random
import edu.uccs.summers.data.geometry.shapes.Point

object PopulationFactory {
  val rnd = Random
  
  def createPerson(id : String, descriptor : PopulationArchetypeDescriptor, area : Area) : Person = {
    val person = Person(id, null, area.generateSpawnPoint, Point(rnd.nextInt(10), rnd.nextInt(10)), area)
    person.executor = descriptor.behavior.executor(person)
    return person
  }
}