package edu.uccs.summers.data.geometry

import scala.collection.immutable
import scala.collection.mutable
import scala.util.Random
import org.jbox2d.common.Vec2
import org.jbox2d.dynamics.World
import edu.uccs.summers.data.Person
import edu.uccs.summers.data.dto.HasDTO
import edu.uccs.summers.data.dto.geometry.{Area => AreaDTO}
import edu.uccs.summers.data.population.InitialPopulationParameters
import edu.uccs.summers.data.population.PopulationFactory
import org.jbox2d.callbacks.ContactListener
import org.jbox2d.dynamics.contacts.Contact
import org.jbox2d.callbacks.ContactImpulse
import org.jbox2d.collision.Manifold

class Area(val name : String, val boundingShape : AreaBounds, val objects : List[StaticEntity], val pop : immutable.Set[Person], rnd : Random) extends Serializable with HasDTO[AreaDTO]{
  
  private var world : World = null
  
  def initialize(){
    world = new World(new Vec2(0f, 0f))
    boundingShape.init(world)
    objects.foreach(_.init(world))
    pop.foreach(_.init(world, this))
    world.setContactListener(new ContactListener(){
      def beginContact(contact : Contact) = {
        (contact.getFixtureA().getBody().getUserData(), contact.getFixtureB().getUserData()) match {
          case (a : Person, b : Person) => {
            a.addVisualContact(b)
          }
          case _ => {}
        }
        (contact.getFixtureA().getUserData(), contact.getFixtureB().getBody().getUserData()) match {
          case (a : Person, b : Person) => {
            a.addVisualContact(b)
          }
          case _ => {}
        }
      }
    
      def endContact(contact : Contact) = {
        (contact.getFixtureA().getBody().getUserData(), contact.getFixtureB().getUserData()) match {
          case (a : Person, b : Person) => {
            a.removeVisualContact(b)
          }
          case _ => {}
        }
        (contact.getFixtureA().getUserData(), contact.getFixtureB().getBody().getUserData()) match {
          case (a : Person, b : Person) => {
            a.removeVisualContact(b)
          }
          case _ => {}
        }
      }

      def preSolve(contact : Contact, oldManifold : Manifold) = {
        
      }
    
      def postSolve(contact : Contact, impulse : ContactImpulse) = {
        
      }
    })
  }
  def generateSpawnPoint() : Vec2 = {
    boundingShape.shape.generatePointWithin(Area.rnd)
  }
  
  def update() = {
    pop.foreach(_.update(this, pop))
    for(i <- 0 to 60){
      world.step(1/60.0f, 3, 12)
    }
  }

  def translate() : AreaDTO = {
    new AreaDTO(name, boundingShape.translate, objects.map(_.translate), pop.map(_.translate))
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
      0 to count foreach (i => {
        pop += PopulationFactory.createPerson(name + "_" + i, fixmeType)
      })
      pop.toSet
    }, rnd)
  }
}