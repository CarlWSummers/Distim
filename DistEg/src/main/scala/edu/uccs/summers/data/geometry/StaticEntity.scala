package edu.uccs.summers.data.geometry

import edu.uccs.summers.data.geometry.shapes.Shape
import java.awt.Graphics2D
import java.awt.Color
import edu.uccs.summers.data.population.PhysicalProperties
import org.jbox2d.dynamics.World
import org.jbox2d.common.Vec2

abstract class StaticEntity(shape : Shape) extends Serializable {
  
  def draw(g : Graphics2D, convertScalar : Float => Float, convertVec2 : Vec2 => Vec2) : Unit
  def init(world : World) = {}
  def getColor() : Color
}