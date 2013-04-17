package edu.uccs.summers.data.population

import edu.uccs.summers.data.Person
import edu.uccs.summers.data.geometry.Area
import edu.uccs.summers.data.behaviors.Behavior

object PopulationFactory {

  def createPerson(id : String, descriptor : PopulationArchetypeDescriptor, area : Area) : Person = {
    val person = Person(id, null, area.generateSpawnPoint)
    person.executor = descriptor.behavior.executor(person)
    return person
  }
}