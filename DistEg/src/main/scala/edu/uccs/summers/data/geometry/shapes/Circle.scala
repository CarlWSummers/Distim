package edu.uccs.summers.data.geometry.shapes

import java.awt.Graphics2D
import scala.util.Random

class Circle(center : Point, radius : Int) extends Shape {
  def draw(g : Graphics2D) = {
    g.fillOval(center.x.toInt - radius, center.y.toInt - radius, radius * 2, radius * 2);
  }
  
  def generatePointWithin(rnd : Random) : Point = {
    center
  }
    
  override def resolveCollision(pos : Point, vel : Point) : Option[(Point, Point)] = {
    if((pos - center).mag < radius){
      Some((pos, new Point(-vel.x, -vel.y)))
    }
    None
  }
}

object Circle {
  def apply(center : Point, radius : Int) = new Circle(center, radius)
}