package edu.uccs.summers.data.geometry

import edu.uccs.summers.data.population.PhysicalProperties
import edu.uccs.summers.math.Algorithms
import edu.uccs.summers.data.geometry.shapes.Shape
import edu.uccs.summers.data.geometry.shapes.Rectangle
import edu.uccs.summers.data.geometry.shapes.Circle
import edu.uccs.summers.data.geometry.shapes.Vec2d
import edu.uccs.summers.data.geometry.shapes.Polygon

trait Collidable {

  def getShape() : Shape
  
  lazy val aabb = {
    this.getShape match {
      case t : Circle => {
        Rectangle(Vec2d(-t.radius, -t.radius) + t.center, t.radius, t.radius)
      }
      case t : Rectangle => {
        t
      }
      case t : Polygon => {
        val minMaxTuple = t.points.foldLeft((Double.MaxValue, Double.MinValue, Double.MaxValue, Double.MinValue))((minMaxTuple, point) => {
          // (xMin, xMax, yMin, yMax)
          (Math.min(minMaxTuple._1, point.x), Math.max(minMaxTuple._2, point.x), Math.min(minMaxTuple._3, point.y), Math.max(minMaxTuple._4, point.y))
        })
        Rectangle(Vec2d(minMaxTuple._1, minMaxTuple._3), Vec2d(minMaxTuple._2, minMaxTuple._4))
      }
    }
  }
}