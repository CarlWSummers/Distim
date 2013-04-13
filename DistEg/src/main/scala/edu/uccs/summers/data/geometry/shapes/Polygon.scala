package edu.uccs.summers.data.geometry.shapes

import java.awt.Graphics2D

class Polygon(val points : List[Point]) extends Shape {
  import language.implicitConversions
  
  def draw(g : Graphics2D) = {
    g.fillPolygon(this)
  }
  
  implicit def PolygonToPolygon(p : Polygon) : java.awt.Polygon = {
    val poly = new java.awt.Polygon()
    p.points.foreach(pnt => poly.addPoint(pnt.x, pnt.y))
    poly
  }
}

object Polygon {
  def apply(points : List[Point]) = new Polygon(points)
}