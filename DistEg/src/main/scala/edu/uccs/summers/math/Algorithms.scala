package edu.uccs.summers.math

import edu.uccs.summers.data.geometry.shapes.Point

object Algorithms {
  /**
   * Computes the intersection between two lines.
   * (c) 2007 Alexander Hristov. Use Freely (LGPL license). http://www.ahristov.com
   * From : http://www.ahristov.com/tutorial/geometry-games/intersection-lines.html
   * 
   * @return Point where the segments intersect, or null if they don't
   */
  def intersection(a1 : Point, a2 : Point, b1 : Point, b2 : Point) : Option[Point] = {
    
    val d = (a1.x-a2.x)*(b1.y-b2.y) - (a1.y-a2.y)*(b1.x-b2.x)
    if (d == 0) return None
    
    val xi = ((b1.x-b2.x)*(a1.x*a2.y-a1.y*a2.x)-(a1.x-a2.x)*(b1.x*b2.y-b1.y*b2.x))/d
    val yi = ((b1.y-b2.y)*(a1.x*a2.y-a1.y*a2.x)-(a1.y-a2.y)*(b1.x*b2.y-b1.y*b2.x))/d
    
    return Some(Point(xi,yi))
  }
  
  def leftHandNormal(p1 : Point, p2 : Point) : Point = {
    val dx = p2.x - p1.x
    val dy = p2.y - p1.y
    new Point(dy, -dx)
  }
}