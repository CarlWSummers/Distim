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
import edu.uccs.summers.data.dto.geometry.{Wall => WallDTO}

class Wall(shape : Shape) extends StaticEntity(shape) with Serializable {

  private var body : Body = null
  def isCollidable : Boolean = true
  
  def getShape() = shape
  
  override def init(world : World) : Unit = {
    val bodyDef = new BodyDef
    bodyDef.`type` = BodyType.STATIC
    bodyDef.allowSleep = true
    bodyDef.fixedRotation = true
    bodyDef.position = shape.getOrigin
    body = world.createBody(bodyDef)
    body.setUserData(this)
    
    val fixDef : FixtureDef = new FixtureDef
    fixDef.shape = shape.createCollidable
    body.createFixture(fixDef)
  }
  
//  override def draw(g : Graphics2D, convertScalar : Float => Float, convertVec2 : Vec2 => Vec2) : Unit = {
//    val oldColor = g.getColor;
//    g.setColor(getColor)
//    shape.draw(g, convertScalar, convertVec2)
//    g.setColor(oldColor)
//  }

  def translate() : WallDTO = {
    new WallDTO(shape)
  }
  
}

object Wall{
  def apply(shape : Shape) = new Wall(shape)
}