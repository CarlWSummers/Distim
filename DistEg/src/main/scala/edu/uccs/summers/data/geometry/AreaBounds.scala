package edu.uccs.summers.data.geometry

import edu.uccs.summers.data.geometry.shapes.Shape

class AreaBounds(val shape : Shape) extends Collidable {

  def getShape() = shape
}