package edu.uccs.summers.data.geometry

import edu.uccs.summers.data.geometry.shapes.Shape
import java.awt.Color

class Exit(shape : Shape) extends StaticEntity(shape) {
  
  def getColor() : Color = {
    Color.GRAY
  }
}

object Exit{
  def apply(shape : Shape) = new Exit(shape)
}