package edu.uccs.summers.data.geometry

import edu.uccs.summers.data.geometry.shapes.Shape
import java.awt.Color
import edu.uccs.summers.data.geometry.shapes.Vec2d

class Wall(shape : Shape) extends StaticEntity(shape) with Collidable{
  def isCollidable : Boolean = true
  
  def getColor() : Color = {
    Color.BLACK
  }
  
  def getShape() = shape
}

object Wall{
  def apply(shape : Shape) = new Wall(shape)
}