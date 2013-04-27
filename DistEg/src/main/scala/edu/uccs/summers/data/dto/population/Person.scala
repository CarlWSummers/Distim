package edu.uccs.summers.data.dto.population

import edu.uccs.summers.data.dto.DTOType
import java.awt.Graphics2D
import org.jbox2d.common.Vec2
import java.awt.geom.Ellipse2D
import java.awt.Color
import java.awt.geom.Line2D

class Person(val position : Vec2, val velocity : Vec2, val angle : Float) extends DTOType {

  def draw(g : Graphics2D, convertScalar : Float => Float, convertVec2 : Vec2 => Vec2) = {
    val worldPos = convertVec2(position)
    val radius = convertScalar(.75f)
    val diameter = 2 * radius
    val shape = new Ellipse2D.Float(-radius, -radius, diameter, diameter)
    
    val oldTransform = g.getTransform
    g.translate(worldPos.x, worldPos.y)
    g.setColor(Color.YELLOW)
    g.fill(shape)
    g.setColor(Color.GREEN)
    val pixelVelocity = convertVec2(velocity)
    g.draw(new Line2D.Float(0, 0, pixelVelocity.x, pixelVelocity.y))
    g.setColor(Color.RED)
    g.rotate(angle.toDouble)
    g.draw(new Line2D.Float(0, 0, 0, -radius))
    g.setTransform(oldTransform)
  }
}