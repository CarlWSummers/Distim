package edu.uccs.summers.data.geometry

import edu.uccs.summers.data.geometry.shapes.Shape

class Exit(shape : Shape) extends StaticEntity(shape) {
  def isCollidable : Boolean = false
}

object Exit{
  def apply(shape : Shape) = new Exit(shape)
}