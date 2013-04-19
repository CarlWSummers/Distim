package edu.uccs.summers.data.geometry

import edu.uccs.summers.data.geometry.shapes.Shape
import java.awt.Color
import edu.uccs.summers.data.geometry.shapes.Point

class Wall(shape : Shape) extends StaticEntity(shape) {
  def isCollidable : Boolean = true
  
  def getColor() : Color = {
    Color.BLACK
  }
}

object Wall{
  def apply(shape : Shape) = new Wall(shape)
}