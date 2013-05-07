package edu.uccs.summers.data.geometry

import edu.uccs.summers.data.geometry.shapes.Shape
import edu.uccs.summers.data.dto.geometry.{StaticEntity => StaticEntityDTO}
import java.awt.Color
import org.jbox2d.common.Vec2
import java.awt.Graphics2D
import edu.uccs.summers.data.dto.geometry.{Exit => ExitDTO}
import org.jbox2d.dynamics.World
import org.jbox2d.dynamics.BodyDef
import org.jbox2d.dynamics.Body
import org.jbox2d.dynamics.BodyType
import org.jbox2d.dynamics.FixtureDef
import org.jbox2d.dynamics.Filter
import edu.uccs.summers.data.Person

class Exit(shape : Shape) extends StaticEntity(shape) with Serializable {

  var body : Body = null
  
  override def init(world : World) = {
    val bodyDef = new BodyDef
    bodyDef.`type` = BodyType.STATIC
    bodyDef.allowSleep = true
    bodyDef.fixedRotation = true
    bodyDef.position = shape.getOrigin
    body = world.createBody(bodyDef)
    body.setUserData(this)
    
    val fixDef : FixtureDef = new FixtureDef
    fixDef.shape = shape.createCollidable
    fixDef.filter = new Filter
    fixDef.filter.categoryBits = Exit.BODY_CATEGORY
    fixDef.filter.maskBits = Person.BODY_CATEGORY
    fixDef.isSensor = true
    body.createFixture(fixDef).setUserData(this)
  }
  
  def translate() : StaticEntityDTO = {
    new ExitDTO(shape)
  }
}

object Exit{
  val BODY_CATEGORY = 0x0010
  def apply(shape : Shape) = new Exit(shape)
}