package edu.uccs.summers.data.geometry.shapes

import java.awt.Graphics2D
import scala.util.Random
import java.awt.geom.Line2D
import org.jbox2d.common.Vec2
import org.jbox2d.collision.shapes.PolygonShape
import java.awt.geom.Path2D

class Polygon(val points : List[Vec2]) extends Shape {
  
  override def draw(g : Graphics2D, convertScalar : Float => Float, convertVec2 : Vec2 => Vec2) = {
    g.fill(getDrawable(convertScalar, convertVec2))
  }
  
  def generatePointWithin(rnd : Random) : Vec2 = {
    points.head
  }
  
  override def getDrawable(convertScalar : Float => Float, convertVec2 : Vec2 => Vec2) : java.awt.Shape = {
    val poly = new Path2D.Float()
    val origin = convertVec2(getOrigin)
    poly.moveTo(origin.x * 2, origin.y * 2)
    points.tail.map(pnt => convertVec2(pnt)).foreach(pnt => poly.lineTo((pnt.x + origin.x).toInt, (pnt.y + origin.y).toInt))
    poly.closePath()
    poly
  }
  

  
  override def getOrigin() = {
    points.head
  }
  
  override def createCollidable() = {
    val poly = new PolygonShape
    poly.set(points.toArray, points.size)
    poly
  }
}

object Polygon {
  def apply(points : List[Vec2]) = new Polygon(points)
}