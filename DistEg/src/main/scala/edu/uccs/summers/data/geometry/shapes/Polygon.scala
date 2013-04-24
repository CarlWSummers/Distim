package edu.uccs.summers.data.geometry.shapes

import java.awt.Graphics2D
import scala.util.Random
import java.awt.geom.Line2D
import org.jbox2d.common.Vec2
import org.jbox2d.collision.shapes.PolygonShape

class Polygon(val points : List[Vec2]) extends Shape {
  
  override def draw(g : Graphics2D, convertScalar : Float => Float, convertVec2 : Vec2 => Vec2) = {
    val poly = new java.awt.Polygon()
    val origin = convertVec2(getOrigin)
    points.map(pnt => convertVec2(pnt)).foreach(pnt => poly.addPoint((pnt.x + origin.x).toInt, (pnt.y + origin.y).toInt))
    g.drawPolygon(poly)
  }
  
//  override def draw(g : Graphics2D, convertScalar : Float => Float, convertVec2 : Vec2 => Vec2) = {
//    val pixelUL = convertVec2(ul)
//    g.fillRect(pixelUL.x.toInt, pixelUL.y.toInt, Math.round(convertScalar(width)).toInt, Math.round(convertScalar(height)).toInt);
//  }
  
  def generatePointWithin(rnd : Random) : Vec2 = {
    points.head
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