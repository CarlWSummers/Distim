package edu.uccs.summers.data.geometry.shapes

import java.awt.Graphics2D
import scala.util.Random
import java.awt.geom.Line2D
import edu.uccs.summers.math.Algorithms


class Polygon(val points : List[Vec2d]) extends Shape {
  import language.implicitConversions
  
  def draw(g : Graphics2D) = {
    g.fillPolygon(this)
  }
  
  def generatePointWithin(rnd : Random) : Vec2d = {
    points.head
  }
  
  implicit def PolygonToPolygon(p : Polygon) : java.awt.Polygon = {
    val poly = new java.awt.Polygon()
    p.points.foreach(pnt => poly.addPoint(pnt.x.toInt, pnt.y.toInt))
    poly
  }
}

object Polygon {
  def apply(points : List[Vec2d]) = new Polygon(points)
}