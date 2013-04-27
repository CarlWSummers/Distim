package edu.uccs.summers.data.dto.geometry

import edu.uccs.summers.data.dto.DTOType
import java.awt.Graphics2D
import org.jbox2d.common.Vec2

class AreaTransition extends StaticEntity with DTOType {

  def draw(g : Graphics2D, convertScalar : Float => Float, convertVec2 : Vec2 => Vec2) = {
    throw new UnsupportedOperationException
  }
}