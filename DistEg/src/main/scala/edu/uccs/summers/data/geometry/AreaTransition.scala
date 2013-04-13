package edu.uccs.summers.data.geometry

import edu.uccs.summers.data.geometry.shapes.Shape
import java.awt.Graphics2D
import java.awt.Color

class AreaTransition(name : String, s : Shape, destArea : String, destTransition : String) extends StaticEntity(s) {
  def isCollidable = false
  override def draw(g : Graphics2D){
    val oldColor = g.getColor()
    g.setColor(Color.YELLOW)
    super.draw(g)
    g.setColor(oldColor);
  }
}

object AreaTransition{
  def apply(name : String, s : Shape, destArea : String, destTransition : String) = 
    new AreaTransition(name, s, destArea, destTransition)
}