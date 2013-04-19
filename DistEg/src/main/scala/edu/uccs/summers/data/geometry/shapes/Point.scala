package edu.uccs.summers.data.geometry.shapes

class Point(val x : Double, val y : Double) {
  
  override def toString() = "(" + x + ", " + y + ")"
  
  def +(p : Point) = {
    Point(x + p.x, y + p.y)
  }
  
  def -(p :Point) = {
    Point(x - p.x, y - p.y)
  }
  
  def *(p : Point) = {
    Point(x * p.x, y * p.y)
  }
  
  def *(s : Int) = {
    Point(x * s, y * s)
  }
  
  def *(d : Double) = {
    Point(x * d, y * d)
  }
  
  def mag() = {
    Math.sqrt(x * x + y * y)
  }
  
  def unit() = {
    this / mag
  }
  
  def /(d : Double) = {
    Point(x/d, y/d)
  }
  
  def dot(p : Point) = {
    x * p.x + y * p.y
  }
}

object Point{
  import scala.language.implicitConversions
  
  def apply(x : Double, y : Double) = new Point(x, y)
  implicit def point2Point(p : Point) : java.awt.Point = new java.awt.Point(p.x.toInt, p.y.toInt)
  implicit def int2Point(i : Int) : Point = new Point(i, 1)
  implicit def double2Point(d : Double) : Point = new Point(d, 1)
}
