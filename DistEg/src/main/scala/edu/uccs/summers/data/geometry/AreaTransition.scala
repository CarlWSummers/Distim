package edu.uccs.summers.data.geometry

import edu.uccs.summers.data.geometry.shapes.Shape
import java.awt.Graphics2D
import java.awt.Color
import org.jbox2d.common.Vec2
import edu.uccs.summers.data.dto.geometry.{AreaTransition => AreaTransitionDTO}

class AreaTransition(name : String, s : Shape, destArea : String, destTransition : String) extends StaticEntity(s) with Serializable {

  def translate() : AreaTransitionDTO = {
    new AreaTransitionDTO(destArea, destTransition, s)
  }
}

object AreaTransition{
  def apply(name : String, s : Shape, destArea : String, destTransition : String) = 
    new AreaTransition(name, s, destArea, destTransition)
}