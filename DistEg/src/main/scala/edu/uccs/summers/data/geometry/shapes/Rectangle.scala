package edu.uccs.summers.data.geometry.shapes

import java.awt.Graphics2D
import java.awt.geom.Line2D
import scala.collection.immutable.Seq
import scala.util.Random
import org.jbox2d.common.Vec2
import java.awt.geom.Rectangle2D
import org.jbox2d.collision.shapes.PolygonShape

class Rectangle(val ul : Vec2, val width : Float, val height : Float) extends Shape {
  val points = Seq(new Vec2(0,0), new Vec2(width, 0), new Vec2(width, -height), new Vec2(0, -height))

  override def draw(g : Graphics2D, convertScalar : Float => Float, convertVec2 : Vec2 => Vec2) = {
    val pixelUL = convertVec2(ul)
    g.fill(new Rectangle2D.Float(pixelUL.x, pixelUL.y, convertScalar(width), convertScalar(height)))
  }
  
  def generatePointWithin(rnd : Random) : Vec2 = {
    val x = math.max(ul.x + (rnd.nextFloat * (width - 1)), ul.x + 1) //rnd.nextInt(width.toInt - 1) + 1
    val y = ul.y - (rnd.nextFloat * (height - 1)) - 1 //rnd.nextInt(height.toInt - 1) - 1
    new Vec2(x,y)
  }
  
  def createCollidable = {
    val rect = new PolygonShape
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