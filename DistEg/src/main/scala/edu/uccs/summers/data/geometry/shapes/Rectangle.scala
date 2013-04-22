package edu.uccs.summers.data.geometry.shapes

import java.awt.Graphics2D
import java.awt.geom.Line2D
import scala.collection.immutable.Seq
import scala.util.Random
import edu.uccs.summers.math.Algorithms

class Rectangle(val ul : Vec2d, val width : Double, val height : Double) extends Shape {
  val points = Seq(ul, Vec2d(ul.x + width, ul.y), Vec2d(ul.x + width, ul.y + height), Vec2d(ul.x, ul.y + height), ul)

  def draw(g : Graphics2D) = {
    g.fillRect(ul.x.toInt, ul.y.toInt, Math.round(width).toInt, Math.round(height).toInt);
  }
  
  def generatePointWithin(rnd : Random) : Vec2d = {
    val x = rnd.nextInt(Math.max(ul.x - width, width).toInt) + ul.x
    val y = rnd.nextInt(Math.max(ul.y - height, height).toInt) + ul.y
    Vec2d(x,y)
  }
  
//  override def resolveCollision(pos : Vec2d, vel : Vec2d, ) : Option[(Vec2d, Vec2d)] = { 
//    val anticipatedPos = pos + vel
//    for(i <- 0 to points.length - 2){
//      val intersectOpt = Algorithms.intersection(pos, anticipatedPos, points(i), points(i+1))
//      if(intersectOpt.isDefined){
//        val intersect = intersectOpt.get
//        if(Line2D.ptSegDist(pos.x, pos.y, anticipatedPos.x, anticipatedPos.y, intersect.x, intersect.y) == 0){
//          val normal = Algorithms.normal(points(i), points(i + 1)).unit
//          val distanceAlongNormal = vel dot normal
//          val newVel = Vec2d((vel.x - (2 * distanceAlongNormal) * normal.x) * .90, (vel.y - (2 * distanceAlongNormal) * normal.y) * .90)
//          
//          val intersectRatio = (anticipatedPos - intersect).mag / (intersect - pos).mag
//          val newPos = intersect + (newVel / intersectRatio)
//          return Some((newPos, newVel))
//        }
//      }
//    }
//    None
//  }
  
}

object Rectangle {
  def apply(ul : Vec2d, width : Int, height : Int) = new Rectangle(ul, width, height)
  def apply(ul : Vec2d, lr : Vec2d) = new Rectangle(ul, (lr.x - ul.x).toInt, (lr.y - ul.y).toInt)
}