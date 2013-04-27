package edu.uccs.summers.data.geometry

import edu.uccs.summers.data.geometry.shapes.Shape
import java.awt.Graphics2D
import java.awt.Color
import edu.uccs.summers.data.population.PhysicalProperties
import org.jbox2d.dynamics.World
import org.jbox2d.common.Vec2
import edu.uccs.summers.data.dto.HasDTO
import edu.uccs.summers.data.dto.geometry.{StaticEntity => SeDTO}

abstract class StaticEntity(shape : Shape) extends Serializable with HasDTO[SeDTO]{
  def init(world : World) = {}
}