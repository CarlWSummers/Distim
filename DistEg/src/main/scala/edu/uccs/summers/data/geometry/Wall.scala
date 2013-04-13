package edu.uccs.summers.data.geometry

import edu.uccs.summers.data.geometry.shapes.Shape

class Wall(shape : Shape) extends StaticEntity(shape) {
  def isCollidable : Boolean = true
}

object Wall{
  def apply(shape : Shape) = new Wall(shape)
}