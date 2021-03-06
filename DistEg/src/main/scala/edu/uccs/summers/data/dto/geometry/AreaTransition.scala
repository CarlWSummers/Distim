package edu.uccs.summers.data.dto.geometry

import edu.uccs.summers.data.dto.DTOType
import edu.uccs.summers.data.geometry.shapes.Shape
import java.awt.Graphics2D
import org.jbox2d.common.Vec2
import java.awt.Color

case class AreaTransition(val destinationArea : String, val destinationName : String, shape : Shape) extends StaticEntity with DTOType {
  def draw(g : Graphics2D, convertScalar : Float => Float, convertVec2 : Vec2 => Vec2) = {
    val oldColor = g.getColor()

    val drawable = shape.getDrawable(convertScalar, convertVec2)
    g.setColor(Color.YELLOW.darker)
    g.fill(drawable)
    g.setColor(Color.LIGHT_GRAY.darker)
    g.draw(drawable)
    
    g.setColor(oldColor)
  }
}