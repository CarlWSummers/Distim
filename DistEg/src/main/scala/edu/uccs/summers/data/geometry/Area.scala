package edu.uccs.summers.data.geometry

import java.awt.Graphics2D
import java.awt.Color
import edu.uccs.summers.data.geometry.shapes.Vec2d
import scala.collection._
import scala.util.Random
import edu.uccs.summers.data.geometry.shapes.Shape
import edu.uccs.summers.data.population.InitialPopulationParameters
import org.jbox2d.dynamics.World
import org.jbox2d.common.Vec2
import org.jbox2d.collision.{shapes => jboxShapes}
import edu.uccs.summers.data.geometry.shapes.Circle
import org.jbox2d.dynamics.BodyDef
import org.jbox2d.dynamics.BodyType
import edu.uccs.summers.data.population.PopulationFactory
import edu.uccs.summers.data.Person

class Area(val name : String, val boundingShape : AreaBounds, val objects : List[StaticEntity], val pop : immutable.Set[Person], rnd : Random) {
  
  private val world = new World(new Vec2(0f, 0f))
  boundingShape.init(world)
  objects.foreach(_.init(world))
  pop.foreach(_.init(world, this))
  
  val bgColor = boundingShape.getColor
  
  def draw(g : Graphics2D, convertScalar : Float => Float, convertVec2 : Vec2 => Vec2){
    g.setColor(bgColor)
    boundingShape.draw(g, convertScalar, convertVec2)
    objects.foreach(entity => {
      val matrix = g.getTransform
      entity.draw(g, convertScalar, convertVec2)
      g.setTransform(matrix)
    })
    pop.foreach(person => {
      val matrix = g.getTransform
      person.draw(g, convertScalar, convertVec2)
      g.setTransform(matrix)
    })
  }
  
  def generateSpawnPoint() : Vec2 = {
    boundingShape.shape.generatePointWithin(Area.rnd)
  }
  
  def update() = {
    world.step(1, 3, 12)
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