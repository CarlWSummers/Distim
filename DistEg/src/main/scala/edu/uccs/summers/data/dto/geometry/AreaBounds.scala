package edu.uccs.summers.data.dto.geometry

import edu.uccs.summers.data.dto.DTOType
import java.awt.Graphics2D
import java.awt.Color
import org.jbox2d.common.Vec2
import java.awt.geom.Line2D

class AreaBounds(val origin : Vec2, val edges : Seq[Vec2]) extends StaticEntity with DTOType{
  
  def getColor = Color.GREEN
  
  def draw(g : Graphics2D, convertScalar : Float => Float, convertVec2 : Vec2 => Vec2) {
    g.setColor(Color.BLUE)
    val worldOrigin = convertVec2(origin)
    1 to edges.length-1 foreach (vert => {
      val a = convertVec2(edges(vert - 1)).addLocal(worldOrigin)
      val b = convertVec2(edges(vert)).addLocal(worldOrigin)
      val line = new Line2D.Float()
      line.setLine(a.x, a.y, b.x, b.y)
      g.draw(line)
    })
  }
}