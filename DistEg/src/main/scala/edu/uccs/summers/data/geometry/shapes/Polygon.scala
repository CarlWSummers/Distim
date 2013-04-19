package edu.uccs.summers.data.geometry.shapes

import java.awt.Graphics2D
import scala.util.Random
import java.awt.geom.Line2D
import edu.uccs.summers.math.Algorithms._


class Polygon(val points : List[Point]) extends Shape {
  import language.implicitConversions
  
  def draw(g : Graphics2D) = {
    g.fillPolygon(this)
  }
  
  def generatePointWithin(rnd : Random) : Point = {
    points.head
  }
  
  implicit def PolygonToPolygon(p : Polygon) : java.awt.Polygon = {
    val poly = new java.awt.Polygon()
    p.points.foreach(pnt => poly.addPoint(pnt.x.toInt, pnt.y.toInt))
    poly
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

object Polygon {
  def apply(points : List[Point]) = new Polygon(points)
}