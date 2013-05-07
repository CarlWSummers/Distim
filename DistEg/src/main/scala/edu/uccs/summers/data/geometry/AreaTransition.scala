package edu.uccs.summers.data.geometry

import edu.uccs.summers.data.geometry.shapes.Shape
import java.awt.Graphics2D
import java.awt.Color
import org.jbox2d.common.Vec2
import edu.uccs.summers.data.dto.geometry.{AreaTransition => AreaTransitionDTO}
import org.jbox2d.dynamics.World
import org.jbox2d.dynamics.BodyDef
import org.jbox2d.dynamics.FixtureDef
import org.jbox2d.dynamics.Body
import org.jbox2d.dynamics.BodyType
import org.jbox2d.dynamics.Filter
import edu.uccs.summers.data.Person

class AreaTransition(name : String, s : Shape, val destArea : String, val destName : String) extends StaticEntity(s) with Serializable {

  var body : Body = null
  
  override def init(world : World){
    val bodyDef = new BodyDef
    bodyDef.`type` = BodyType.STATIC
    bodyDef.allowSleep = true
    bodyDef.fixedRotation = true
    bodyDef.position = s.getOrigin
    body = world.createBody(bodyDef)
    body.setUserData(this)
    
    val fixDef : FixtureDef = new FixtureDef
    fixDef.shape = s.createCollidable
    fixDef.filter = new Filter
    fixDef.filter.categoryBits = AreaTransition.BODY_CATEGORY
    fixDef.filter.maskBits = Person.BODY_CATEGORY
    fixDef.isSensor = true
    body.createFixture(fixDef).setUserData(this)
  }
  
  def translate() : AreaTransitionDTO = {
    new AreaTransitionDTO(destArea, destName, s)
  }
}

object AreaTransition{
  val BODY_CATEGORY = 0x0020
  def apply(name : String, s : Shape, destArea : String, destTransition : String) = 
    new AreaTransition(name, s, destArea, destTransition)
}