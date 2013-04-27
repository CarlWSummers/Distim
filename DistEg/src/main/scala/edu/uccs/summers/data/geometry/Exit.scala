package edu.uccs.summers.data.geometry

import edu.uccs.summers.data.geometry.shapes.Shape
import edu.uccs.summers.data.dto.geometry.{StaticEntity => StaticEntityDTO}
import java.awt.Color
import org.jbox2d.common.Vec2
import java.awt.Graphics2D
import edu.uccs.summers.data.dto.geometry.{Exit => ExitDTO}

class Exit(shape : Shape) extends StaticEntity(shape) with Serializable {

  def translate() : StaticEntityDTO = {
    new ExitDTO
  }
}

object Exit{
  def apply(shape : Shape) = new Exit(shape)
}