package edu.uccs.summers.data.geometry

import scala.collection.immutable
import scala.collection.mutable
import scala.util.Random
import org.jbox2d.common.Vec2
import org.jbox2d.dynamics.World
import edu.uccs.summers.data.Person
import edu.uccs.summers.data.dto.HasDTO
import edu.uccs.summers.data.dto.geometry.{Area => AreaDTO}
import edu.uccs.summers.data.geometry.util.VisionListener
import edu.uccs.summers.data.population.InitialPopulationParameters
import edu.uccs.summers.data.population.PopulationFactory
import org.jbox2d.callbacks.RayCastCallback
import org.jbox2d.dynamics.Fixture

class Area(val name : String, val boundingShape : AreaBounds, val objects : List[StaticEntity], val pop : mutable.Set[Person], rnd : Random) extends Serializable with HasDTO[AreaDTO]{
  
  private var world : World = null
  private var elapsedTime : Float = 0
  private val contactListener = new VisionListener
  
  def initialize(){
    world = new World(new Vec2(0f, 0f))
    boundingShape.init(world)
    objects.foreach(_.init(world))
    pop.foreach(_.init(world, this))
    world.setContactListener(contactListener)
  }
  
  def generateSpawnPoint() : Vec2 = {
    boundingShape.shape.generatePointWithin(Area.rnd)
  }
  
  def update() = {
    pop.foreach(_.update(this, pop))
    for(i <- 0 to 5){
      world.step(1/60.0f, 3, 12)
      pop.foreach(person => {
        var filter = raycastFilter(person)(_)
        person.visualContacts = person.visualContacts.filter(filter)
      })
      contactListener.exitingPeople.foreach(person => {
        person.terminate(world)
        pop -= person
      })
      contactListener.clearExitingPeople
    }
    elapsedTime += 1/6f
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