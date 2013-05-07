package edu.uccs.summers.data.dto.geometry

import edu.uccs.summers.data.dto.DTOType
import edu.uccs.summers.data.geometry.shapes.Shape
import java.awt.Graphics2D
import org.jbox2d.common.Vec2
import java.awt.Color

class Destination(val name : String, shape : Shape) extends StaticEntity with DTOType{
  
  override def draw(g : Graphics2D, convertScalar : Float => Float, convertVec2 : Vec2 => Vec2){
    val oldColor = g.getColor()

    val drawable = shape.getDrawable(convertScalar, convertVec2)
    g.setColor(Color.LIGHT_GRAY)
    g.fill(drawable)
    g.setColor(Color.LIGHT_GRAY.darker())
    g.draw(drawable)
    
    
    g.setColor(oldColor)
  }
}