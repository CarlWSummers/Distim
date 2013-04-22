package edu.uccs.summers.data.geometry

import edu.uccs.summers.data.geometry.shapes.Shape
import java.awt.Graphics2D
import java.awt.Color
import edu.uccs.summers.data.geometry.shapes.Vec2d
import edu.uccs.summers.data.population.PhysicalProperties
import edu.uccs.summers.math.Algorithms

abstract class StaticEntity(shape : Shape) {
  def draw(g : Graphics2D) : Unit = {
    val oldColor = g.getColor;
    g.setColor(getColor)
    shape.draw(g)
    g.setColor(oldColor)
  }
  
  def getColor() : Color
}