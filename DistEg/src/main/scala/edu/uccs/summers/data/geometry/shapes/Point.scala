package edu.uccs.summers.data.geometry.shapes

class Point(val x : Int, val y : Int) {

  implicit def Point2Point(p : Point) : java.awt.Point = new java.awt.Point(p.x, p.y)
}

object Point{
  def apply(x : Int, y : Int) = new Point(x, y)
}
