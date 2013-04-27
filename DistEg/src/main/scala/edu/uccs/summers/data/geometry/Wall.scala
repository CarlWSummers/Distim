package edu.uccs.summers.data.geometry

import java.awt.Color
import org.jbox2d.collision.{shapes => jBoxShapes}
import org.jbox2d.dynamics.BodyDef
import org.jbox2d.dynamics.BodyType
import org.jbox2d.dynamics.World
import edu.uccs.summers.data.geometry.shapes.Circle
import edu.uccs.summers.data.geometry.shapes.Shape
import edu.uccs.summers.data.geometry.shapes.Rectangle
import edu.uccs.summers.data.geometry.shapes.Polygon
import org.jbox2d.dynamics.FixtureDef
import java.awt.Graphics2D
import org.jbox2d.dynamics.Body
import org.jbox2d.common.Vec2
import org.jbox2d.serialization.pb.PbSerializer
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import org.jbox2d.serialization.pb.PbDeserializer

class Wall(shape : Shape) extends StaticEntity(shape) with Serializable {

  @transient
  private var world : World = null
  @transient
  private var body : Body = null
  def isCollidable : Boolean = true
  
  def getColor() : Color = {
    Color.WHITE
  }
  
  def getShape() = shape
  
  override def init(world : World) : Unit = {
    val bodyDef = new BodyDef
    bodyDef.`type` = BodyType.STATIC
    bodyDef.allowSleep = true
    bodyDef.fixedRotation = true
    bodyDef.position = shape.getOrigin
    body = world.createBody(bodyDef)
    
    val fixDef : FixtureDef = new FixtureDef
    fixDef.shape = shape.createCollidable
    body.createFixture(fixDef)
  }
  
  override def draw(g : Graphics2D, convertScalar : Float => Float, convertVec2 : Vec2 => Vec2) : Unit = {
    val oldColor = g.getColor;
    g.setColor(getColor)
    shape.draw(g, convertScalar, convertVec2)
    g.setColor(oldColor)
  }

  private def writeObject(out: ObjectOutputStream): Unit = {
    val serializer = new PbSerializer
    out.write(serializer.serializeWorld(world).build.toByteArray())
    out.write(serializer.serializeBody(body).build.toByteArray())
    out.defaultWriteObject();
  }
  
  private def readObject(in: ObjectInputStream): Unit = {
    val deserializer = new PbDeserializer
    world = deserializer.deserializeWorld(in)
    body = deserializer.deserializeBody(world, in)
    in.defaultReadObject()
  }

}

object Wall{
  def apply(shape : Shape) = new Wall(shape)
}