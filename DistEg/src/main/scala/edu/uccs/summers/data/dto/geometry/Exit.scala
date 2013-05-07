package edu.uccs.summers.data.dto.geometry

import java.awt.Graphics2D
import org.jbox2d.common.Vec2
import edu.uccs.summers.data.dto.DTOType
import edu.uccs.summers.data.geometry.shapes.Shape
import java.awt.Color

case class Exit(val shape : Shape) extends StaticEntity with DTOType {

  def draw(g : Graphics2D, convertScalar : Float => Float, convertVec2 : Vec2 => Vec2) = {
    val oldColor = g.getColor()

    val drawable = shape.getDrawable(convertScalar, convertVec2)
    g.setColor(Color.GREEN.brighter)
    g.fill(drawable)
    g.setColor(Color.DARK_GRAY)
    g.draw(drawable)
    
    g.setColor(oldColor)
  }
}