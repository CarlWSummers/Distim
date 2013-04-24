package edu.uccs.summers.data.geometry

import edu.uccs.summers.data.geometry.shapes.Shape
import java.awt.Graphics2D
import java.awt.Color
import org.jbox2d.common.Vec2

class AreaTransition(name : String, s : Shape, destArea : String, destTransition : String) extends StaticEntity(s) {
  def isCollidable = false
  override def draw(g : Graphics2D, convertScalar : Float => Float, convertVec2 : Vec2 => Vec2){
    throw new UnsupportedOperationException
  }
  
  def getColor() : Color = {
    Color.CYAN
  }
}

object AreaTransition{
  def apply(name : String, s : Shape, destArea : String, destTransition : String) = 
    new AreaTransition(name, s, destArea, destTransition)
}