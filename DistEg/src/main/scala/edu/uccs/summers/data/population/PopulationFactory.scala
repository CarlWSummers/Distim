package edu.uccs.summers.data.population

import edu.uccs.summers.data.Person
import edu.uccs.summers.data.geometry.Area
import edu.uccs.summers.data.behaviors.Behavior
import scala.util.Random
import org.jbox2d.common.Vec2
import java.awt.Color

object PopulationFactory {
  val rnd = new java.util.Random
  
  def createPerson(id : String, descriptor : PopulationArchetypeDescriptor) : Person = {
    val person = Person(id, null, PhysicalProperties(new Vec2(0, 0), new Vec2(rnd.nextInt(10) - 5, rnd.nextInt(10) - 5), 30, 1))
    person.executor = descriptor.behavior.executor(person)
    person.color = Color.getHSBColor(rnd.nextFloat, (rnd.nextInt(9000)) / 10000f, 0.9f)
    return person
  }
}