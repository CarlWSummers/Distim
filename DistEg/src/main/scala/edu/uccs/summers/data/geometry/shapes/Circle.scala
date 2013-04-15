package edu.uccs.summers.data.geometry.shapes

import java.awt.Graphics2D
import scala.util.Random

class Circle(center : Point, radius : Int) extends Shape {
  def draw(g : Graphics2D) = {
    g.fillOval(center.x - radius, center.y - radius, radius * 2, radius * 2);
  }
  
  def generatePointWithin(rnd : Random) : Point = {
    center
  }
}

object Circle {
  def apply(center : Point, radius : Int) = new Circle(center, radius)
}