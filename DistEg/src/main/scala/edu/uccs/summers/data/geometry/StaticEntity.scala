package edu.uccs.summers.data.geometry

import edu.uccs.summers.data.geometry.shapes.Shape
import java.awt.Graphics2D
import java.awt.Color
import edu.uccs.summers.data.geometry.shapes.Point

abstract class StaticEntity(shape : Shape) {
  def draw(g : Graphics2D) : Unit = {
    val oldColor = g.getColor;
    g.setColor(getColor)
    shape.draw(g)
    g.setColor(oldColor)
  }
  
  def resolveCollision(pos : Point, vel : Point) :  Option[(Point, Point)]  = { 
    if(isCollidable) shape.resolveCollision(pos, vel) else None
  }
  
  def isCollidable : Boolean
  def getColor() : Color
}