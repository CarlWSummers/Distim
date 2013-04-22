package edu.uccs.summers.data.geometry

import java.awt.Graphics2D
import java.awt.Color
import edu.uccs.summers.data.geometry.shapes.Vec2d
import scala.collection.immutable.Nil
import scala.util.Random
import edu.uccs.summers.data.geometry.shapes.Shape
import edu.uccs.summers.data.population.InitialPopulationParameters

class Area(val name : String, val boundingShape : AreaBounds, val objects : List[StaticEntity], val popParams : InitialPopulationParameters) {
  
  val bgColor = new Color(25, 99, 40)
  
  def draw(g : Graphics2D){
    g.setColor(bgColor)
    boundingShape.shape.draw(g)
    objects.foreach(entity => {
      entity.draw(g)
      if(entity.isInstanceOf[Collidable]){
        g.setColor(entity.getColor)
        entity.asInstanceOf[Collidable].aabb.draw(g)
      }
    })
  }
  
  def generateSpawnPoint() : Vec2d = {
    boundingShape.shape.generatePointWithin(Area.rnd)
  }
  
  def collidables() : List[Collidable] = {
    objects.flatMap(entity => entity match {
      case e : Collidable => e :: Nil
    })
  }
}

object Area{
  private val rnd = new Random
  def apply(name : String, bounds : AreaBounds, objects : List[StaticEntity], popParams : InitialPopulationParameters) = {
    new Area(name, bounds, objects, popParams)
  }
}