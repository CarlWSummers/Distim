package edu.uccs.summers.math

import edu.uccs.summers.data.population.PhysicalProperties
import edu.uccs.summers.data.geometry.shapes.Shape
import edu.uccs.summers.data.geometry.shapes.Vec2d
import edu.uccs.summers.data.geometry.Collidable
import edu.uccs.summers.data.geometry.shapes.Rectangle

object Algorithms {
  /**
   * Computes the intersection between two lines.
   * (c) 2007 Alexander Hristov. Use Freely (LGPL license). http://www.ahristov.com
   * From : http://www.ahristov.com/tutorial/geometry-games/intersection-lines.html
   * 
   * @return Point where the segments intersect, or null if they don't
   */
  def intersection(a1 : Vec2d, a2 : Vec2d, b1 : Vec2d, b2 : Vec2d) : Option[Vec2d] = {
    
    val d = (a1.x-a2.x)*(b1.y-b2.y) - (a1.y-a2.y)*(b1.x-b2.x)
    if (d == 0) return None
    
    val xi = ((b1.x-b2.x)*(a1.x*a2.y-a1.y*a2.x)-(a1.x-a2.x)*(b1.x*b2.y-b1.y*b2.x))/d
    val yi = ((b1.y-b2.y)*(a1.x*a2.y-a1.y*a2.x)-(a1.y-a2.y)*(b1.x*b2.y-b1.y*b2.x))/d
    
    return Some(Vec2d(xi,yi))
  }
  
  /**
   * Returns the left normal of the given line
   */
  def normal(p1 : Vec2d, p2 : Vec2d) : Vec2d = {
    new Vec2d(-p2.y - p1.y, p2.x - p1.x)
  }
  
  def resolveCollisions(obj : PhysicalProperties, environment : List[Collidable]) : PhysicalProperties = {
    val halfWidth = Math.round(obj.width / 2).toInt
    val proposedPosition = obj.position + obj.velocity
    val objAabb = Rectangle(proposedPosition - Vec2d(halfWidth, halfWidth), obj.width.toInt, obj.width.toInt)
    val collidableObjects = environment.flatMap(collidable => {
      if(doCollide(objAabb, collidable.aabb)) collidable :: Nil
      else Nil
    })
    println(collidableObjects.size)
//            
//        area.objects.foreach(t => {
//          t match {
//            case t : Collidable => {
//              val collision = t.resolveCollision(currentPos, currentVel)
//              collision.foreach( p => {
//                currentPos = p._1
//                currentVel = p._2
//                Person(person.id, person.executor, p._1, p._2, area)
//              })
//            }
//            case _ => {}
//          }
//        })
//        Person(person.id, person.executor, currentPos + currentVel, currentVel, area)
    PhysicalProperties(proposedPosition, obj.velocity, obj.width, obj.mass)
  }
  
  def doCollide(a : Rectangle, b : Rectangle) : Boolean = {
    if (a.ul.x + a.width < b.ul.x || b.ul.x + b.width < a.ul.x) return false
    if (a.ul.y + a.height < b.ul.y || b.ul.y + b.height < a.ul.y) return false

    
    return true
  }
  
}

