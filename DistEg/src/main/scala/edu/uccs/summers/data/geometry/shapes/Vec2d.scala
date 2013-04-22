package edu.uccs.summers.data.geometry.shapes

class Vec2d(val x : Double, val y : Double) {
  
  override def toString() = "(" + x + ", " + y + ")"
  
  def +(p : Vec2d) = {
    Vec2d(x + p.x, y + p.y)
  }
  
  def -(p :Vec2d) = {
    Vec2d(x - p.x, y - p.y)
  }
  
  def *(p : Vec2d) = {
    Vec2d(x * p.x, y * p.y)
  }
  
  def *(s : Int) = {
    Vec2d(x * s, y * s)
  }
  
  def *(d : Double) = {
    Vec2d(x * d, y * d)
  }
  
  def mag() = {
    Math.sqrt(x * x + y * y)
  }
  
  def unit() = {
    Vec2d.this / mag
  }
  
  def /(d : Double) = {
    Vec2d(x/d, y/d)
  }
  
  def dot(p : Vec2d) = {
    x * p.x + y * p.y
  }
}

object Vec2d{
  import scala.language.implicitConversions
  
  def apply(x : Double, y : Double) = new Vec2d(x, y)
  implicit def point2Point(p : Vec2d) : java.awt.Point = new java.awt.Point(p.x.toInt, p.y.toInt)
  implicit def int2Point(i : Int) : Vec2d = new Vec2d(i, 1)
  implicit def double2Point(d : Double) : Vec2d = new Vec2d(d, 1)
}
