package edu.uccs.summers.data.geometry

import edu.uccs.summers.data.geometry.shapes.Shape
import java.awt.Color
import org.jbox2d.common.Vec2
import java.awt.Graphics2D

class Exit(shape : Shape) extends StaticEntity(shape) {
  
  def draw(g : Graphics2D, convertScalar : Float => Float, convertVec2 : Vec2 => Vec2) : Unit = {
    throw new UnsupportedOperationException
  }
  
  def getColor() : Color = {
    Color.GRAY
  }
}

object Exit{
  def apply(shape : Shape) = new Exit(shape)
}