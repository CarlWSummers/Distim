package edu.uccs.summers.data.geometry.shapes

import java.awt.Graphics2D
import java.awt.geom.Line2D

import scala.collection.immutable.Seq
import scala.util.Random

import edu.uccs.summers.math.Algorithms._

class Rectangle(val ul : Point, val width : Int, val height : Int) extends Shape {
  val points = Seq(ul, Point(ul.x + width, ul.y), Point(ul.x + width, ul.y + height), Point(ul.x, ul.y + height), ul)
//  val points = Seq(ul, Point(ul.x + width, ul.y + height), Point(ul.x, ul.y + height), Point(ul.x + width, ul.y), ul)

  def draw(g : Graphics2D) = {
    g.fillRect(ul.x.toInt, ul.y.toInt, width, height);
  }
  
  def generatePointWithin(rnd : Random) : Point = {
    val x = rnd.nextInt(Math.max(ul.x.toInt - width, width)) + ul.x
    val y = rnd.nextInt(Math.max(ul.y.toInt - height, height)) + ul.y
    Point(x,y)
  }
  
  override def resolveCollision(pos : Point, vel : Point) : Option[(Point, Point)] = { 
    val anticipatedPos = pos + vel
    for(i <- 0 to points.length - 2){
      val intersectOpt = intersection(pos, anticipatedPos, points(i), points(i+1))
      if(intersectOpt.isDefined){
        val intersect = intersectOpt.get
        if(Line2D.ptSegDist(pos.x, pos.y, anticipatedPos.x, anticipatedPos.y, intersect.x, intersect.y) == 0){
          val normal = leftHandNormal(points(i), points(i + 1)).unit
          val distanceAlongNormal = vel dot normal
          val newVel = Point((vel.x - (2 * distanceAlongNormal) * normal.x) * .90, (vel.y - (2 * distanceAlongNormal) * normal.y) * .90)
          
          val intersectRatio = (anticipatedPos - intersect).mag / (intersect - pos).mag
          val newPos = intersect + (newVel / intersectRatio)
          return Some((newPos, newVel))
        }
      }
    }
    None
  }
}

object Rectangle {
  def apply(ul : Point, width : Int, height : Int) = new Rectangle(ul, width, height)
  def apply(ul : Point, lr : Point) = new Rectangle(ul, (lr.x - ul.x).toInt, (lr.y - ul.y).toInt)
}