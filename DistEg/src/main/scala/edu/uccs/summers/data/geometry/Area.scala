package edu.uccs.summers.data.geometry

import java.awt.Graphics2D
import java.awt.Color
import edu.uccs.summers.data.geometry.shapes.Point
import scala.collection.immutable.Nil
import scala.util.Random

class Area(val name : String, val objects : List[StaticEntity], val popParams : InitialPopulationParameters) {
  
  val bgColor = new Color(25, 99, 40)
  
  def draw(g : Graphics2D){
    objects.foreach(entity => entity.draw(g))
  }
  
  def generateSpawnPoint() : Point = {
    val spawnAreas = objects.flatMap(entity => entity match {
      case e : SpawnArea => e :: Nil
      case _ => Nil
    })
    
    spawnAreas.head.shape.generatePointWithin(Area.rnd)
  }
}

object Area{
  private val rnd = new Random
  def apply(name : String, objects : List[StaticEntity], popParams : InitialPopulationParameters) = {
    new Area(name, objects, popParams)
  }
}