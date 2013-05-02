package edu.uccs.summers.data.dto.geometry

import java.awt.Graphics2D
import org.jbox2d.common.Vec2
import edu.uccs.summers.data.dto.DTOType
import edu.uccs.summers.data.geometry.shapes.Shape

case class Exit(val shape : Shape) extends StaticEntity with DTOType {

  def draw(g : Graphics2D, convertScalar : Float => Float, convertVec2 : Vec2 => Vec2) = {
    shape.draw(g, convertScalar, convertVec2)
  }
}