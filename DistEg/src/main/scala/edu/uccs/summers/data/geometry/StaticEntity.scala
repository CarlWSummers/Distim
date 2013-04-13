package edu.uccs.summers.data.geometry

import edu.uccs.summers.data.geometry.shapes.Shape
import java.awt.Graphics2D

abstract class StaticEntity(shape : Shape) {
  def draw(g : Graphics2D) : Unit = shape.draw(g)
  def isCollidable : Boolean
}