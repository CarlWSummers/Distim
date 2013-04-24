package edu.uccs.summers.data.geometry.shapes

import java.awt.Graphics2D
import java.awt.geom.Line2D
import scala.collection.immutable.Seq
import scala.util.Random
import org.jbox2d.common.Vec2

class Rectangle(val ul : Vec2, val width : Float, val height : Float) extends Shape {
  val points = Seq(new Vec2(0,0), new Vec2(width, 0), new Vec2(width, -height), new Vec2(0, -height))

  override def draw(g : Graphics2D, convertScalar : Float => Float, convertVec2 : Vec2 => Vec2) = {
    val pixelUL = convertVec2(ul)
    g.drawRect(pixelUL.x.toInt, pixelUL.y.toInt, Math.round(convertScalar(width)).toInt, Math.round(convertScalar(height)).toInt);
  }
  
  def generatePointWithin(rnd : Random) : Vec2 = {
    val x = ul.x + rnd.nextInt(width.toInt - 1)
    val y = ul.y - rnd.nextInt(height.toInt - 1)
    new Vec2(x,y)
  }
  
  def createCollidable = {
    val rect = new org.jbox2d.collision.shapes.PolygonShape
    val halfWidth = width / 2
    val halfHeight = height / 2
    rect.setAsBox(halfWidth, halfHeight, new Vec2(halfWidth, -halfHeight), 0)
    rect
  }
  
  override def getOrigin() = {
    new Vec2(ul)
  }
}

object Rectangle {
  def apply(ul : Vec2, width : Int, height : Int) = new Rectangle(ul, width, height)
  def apply(ul : Vec2, lr : Vec2) = {
    val width = (lr.x - ul.x)
    val height = (ul.y - lr.y)
    new Rectangle(ul, width, height)
  }
}