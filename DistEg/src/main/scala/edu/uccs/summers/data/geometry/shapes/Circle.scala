package edu.uccs.summers.data.geometry.shapes

import java.awt.Graphics2D
import scala.util.Random
import org.jbox2d.common.Vec2
import org.jbox2d.collision.shapes.CircleShape

class Circle(val center : Vec2, val radius : Float) extends Shape {
  
  def draw(g : Graphics2D, convertScalar : Float => Float, convertVec2 : Vec2 => Vec2) = {
    val worldCenter = convertVec2(center)
    val worldRadius = convertScalar(radius)
    g.drawOval((worldCenter.x - worldRadius).toInt, (worldCenter.y - worldRadius).toInt, (worldRadius * 2).toInt, (worldRadius * 2).toInt);
  }
  
  def generatePointWithin(rnd : Random) : Vec2 = {
    center
  }
    
  override def createCollidable = {
    val circle = new CircleShape
    circle.setRadius(radius)
    circle
  }
  
  override def getOrigin() = center
}

object Circle {
  def apply(center : Vec2, radius : Int) = new Circle(center, radius)
}