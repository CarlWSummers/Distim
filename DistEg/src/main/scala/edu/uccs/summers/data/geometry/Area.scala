package edu.uccs.summers.data.geometry

import scala.collection.mutable
import scala.util.Random
import org.jbox2d.callbacks.RayCastCallback
import org.jbox2d.common.Vec2
import org.jbox2d.dynamics.Fixture
import org.jbox2d.dynamics.World
import edu.uccs.summers.data.Person
import edu.uccs.summers.data.dto.HasDTO
import edu.uccs.summers.data.dto.geometry.{Area => AreaDTO}
import edu.uccs.summers.data.dto.geometry.{TransitionEvent => TransitionEventDTO}
import edu.uccs.summers.data.dto.geometry.{Destination => DestinationDTO}
import edu.uccs.summers.data.geometry.util.VisionListener
import edu.uccs.summers.data.population.InitialPopulationParameters
import edu.uccs.summers.data.population.PopulationFactory
import edu.uccs.summers.data.population.PopulationArchetypeDescriptor
import edu.uccs.summers.data.population.PopulationArchetypeDescriptor
import org.jbox2d.dynamics.contacts.Contact

class Area(val name : String, val boundingShape : AreaBounds, val objects : List[StaticEntity], val pop : mutable.Set[Person], rnd : Random) extends Serializable with HasDTO[AreaDTO]{
  
  private var world : World = null
  private var elapsedTime : Float = 0
  private var contactListener : VisionListener = null
  private var archeTypes : mutable.Map[String, PopulationArchetypeDescriptor] = null
  
  def initialize(popArchTypes : mutable.Map[String, PopulationArchetypeDescriptor]){
    archeTypes = popArchTypes
    
    world = new World(new Vec2(0f, 0f))
    boundingShape.init(world)
    objects.foreach(_.init(world))
    pop.foreach(_.init(world, this, false))
    contactListener = new VisionListener
    world.setContactListener(contactListener)
  }
  
  def generateSpawnPoint() : Vec2 = {
    boundingShape.shape.generatePointWithin(Area.rnd)
  }
  
  def update(incomingTransitionCandidates : Set[TransitionEventDTO]) = {
    
    incomingTransitionCandidates
      .filter(_.areaName == name)
      .foreach(t => {
        val destination = objects
            .filter(_.isInstanceOf[Destination])
            .find(_.asInstanceOf[Destination].name == t.destName)
            .get
            .asInstanceOf[Destination]
        val position = destination.shape.generatePointWithin(rnd)
        val newPerson = t.person.toPerson(archeTypes(t.person.archTypeName), position)
        newPerson.init(world, this, true)
        pop += newPerson
      })
    
    pop.foreach(_.update(this, pop))
    val transitions = mutable.Set[TransitionEvent]()
    for(i <- 0 until 6){
      world.step(1/60.0f, 12, 12)
      pop.foreach(person => {
        var filter = raycastFilter(person)(_)
        person.visualContacts = person.visualContacts.filter(filter)
      })
      contactListener.exitingPeople.foreach(person => {
        person.terminate(world)
        pop.foreach(_.removeVisualContact(person))
        pop -= person
      })
      contactListener.clearExitingPeople
      
      val newTransitions = contactListener.transitioningPeople
      newTransitions.foreach(transition => {
        transition.person.terminate(world)
        pop.foreach(_.removeVisualContact(transition.person))
        pop -= transition.person
      })
      transitions ++= newTransitions
      contactListener.clearTransitioningPeople
    }
    elapsedTime += 1/6f
    transitions
  }

  def translate() : AreaDTO = {
    new AreaDTO(name, boundingShape.translate, objects.map(_.translate), pop.map(_.translate).toSet, elapsedTime)
  }
  
  private def raycastFilter(person : Person)(contact : Person): Boolean = {
    var closestPerson : Person = null
    world.raycast(new RayCastCallback{
      var closest : Float = Float.MaxValue
      override def reportFixture(fixture : Fixture, point : Vec2, normal : Vec2, fraction : Float) : Float = {
        fixture.getUserData() match {
          case w : Wall => {
            if(fraction < closest){
              closest = fraction
              closestPerson = null
            }
            return fraction
          }
          case p : Person => {
            if(fraction < closest){
              closest = fraction
              closestPerson = p
            }
          }
          case _ => {}
        }
        return -1f
      }
    }, person.body.getPosition(), contact.body.getPosition())
    val result = closestPerson != null && closestPerson == contact
    result
  }
}

object Area{
  private val rnd = new Random
  def apply(name : String, bounds : AreaBounds, objects : List[StaticEntity], popParams : InitialPopulationParameters, rnd : Random) = {
    new Area(name, bounds, objects, {
      val maxPop = popParams.maxSize
      val minPop = popParams.minSize
      val count = if(maxPop == minPop) minPop else rnd.nextInt(maxPop - minPop) + minPop
      val fixmeType = popParams.popTypes.head
      val pop = mutable.Set[Person]()
      0 to (count-1) foreach (i => {
        pop += PopulationFactory.createPerson(name + "_" + i, fixmeType)
      })
      pop
    }, rnd)
  }
}