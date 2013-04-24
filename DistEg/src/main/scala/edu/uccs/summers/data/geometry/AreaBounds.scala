package edu.uccs.summers.data.geometry

import java.awt.Color
import org.jbox2d.collision.shapes.ChainShape
import org.jbox2d.dynamics.Body
import org.jbox2d.dynamics.BodyDef
import org.jbox2d.dynamics.BodyType
import org.jbox2d.dynamics.FixtureDef
import org.jbox2d.dynamics.World
import edu.uccs.summers.data.geometry.shapes.Rectangle
import java.awt.Graphics2D
import org.jbox2d.common.Vec2

class AreaBounds(val shape : Rectangle) extends StaticEntity(shape){

  var body : Body = null
  var edges : ChainShape = null
  
  override def init(world : World){
    val bodyDef = new BodyDef
    bodyDef.`type` = BodyType.STATIC
    bodyDef.allowSleep = true
    bodyDef.fixedRotation = true
    val origin = shape.getOrigin
    bodyDef.position = origin
    body = world.createBody(bodyDef)
    
    val fixDef : FixtureDef = new FixtureDef
    edges = new ChainShape
    edges.createLoop(shape.points.toArray, shape.points.size)
    fixDef.shape = edges
    body.createFixture(fixDef)
  }
  
  override def draw(g : Graphics2D, convertScalar : Float => Float, convertVec2 : Vec2 => Vec2) {
    g.setColor(Color.BLUE)
    val worldOrigin = convertVec2(body.getPosition)
    1 to edges.m_count-1 foreach (vert => {
      val a = convertVec2(edges.m_vertices(vert - 1)).addLocal(worldOrigin)
      val b = convertVec2(edges.m_vertices(vert)).addLocal(worldOrigin)
      g.drawLine(a.x.toInt, a.y.toInt, b.x.toInt, b.y.toInt)
    })
  }
  
  override def getColor() = new Color(25, 99, 40)
}