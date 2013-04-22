package edu.uccs.summers.data.geometry.shapes

import java.awt.Graphics2D
import scala.util.Random

class Circle(val center : Vec2d, val radius : Int) extends Shape {
  
  def draw(g : Graphics2D) = {
    g.fillOval(center.x.toInt - radius, center.y.toInt - radius, radius * 2, radius * 2);
  }
  
  def generatePointWithin(rnd : Random) : Vec2d = {
    center
  }
    
}

object Circle {
  def apply(center : Vec2d, radius : Int) = new Circle(center, radius)
}