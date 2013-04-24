package edu.uccs.summers.data

import edu.uccs.summers.data.behaviors.BehaviorExecutor
import edu.uccs.summers.data.behaviors.Behavior
import edu.uccs.summers.data.behaviors.BehaviorExecutor
import scala.util.Random
import edu.uccs.summers.data.geometry.shapes.Vec2d
import edu.uccs.summers.data.geometry.Area
import edu.uccs.summers.data.geometry.shapes.Circle
import edu.uccs.summers.data.population.PhysicalProperties
import org.jbox2d.dynamics.World
import org.jbox2d.dynamics.BodyDef
import org.jbox2d.dynamics.BodyType
import org.jbox2d.dynamics.FixtureDef
import org.jbox2d.collision.shapes.CircleShape
import org.jbox2d.dynamics.Body
import java.awt.Graphics2D
import java.awt.Color
import org.jbox2d.common.Vec2

case class Person(val id : String, private var _executor : BehaviorExecutor, dynamics : PhysicalProperties) {

  var body : Body = null
  def init(world : World, area : Area){
    val bodyDef = new BodyDef
    bodyDef.`type` = BodyType.DYNAMIC
    bodyDef.allowSleep = false
    bodyDef.fixedRotation = true
    bodyDef.position = area.generateSpawnPoint
    bodyDef.linearVelocity = dynamics.velocity
    body = world.createBody(bodyDef)
    
    val fixDef : FixtureDef = new FixtureDef
    fixDef.density = 20
    val circle = new CircleShape
    circle.setRadius(.75f)
    fixDef.shape = circle
    fixDef.restitution = .05f
    body.createFixture(fixDef)
  }
  
  def update(area : Area, pop : Set[Person]) = {
    _executor.execute(area, pop, this)
  }
  
  def draw(g : Graphics2D, convertScalar : Float => Float, convertVec2 : Vec2 => Vec2) = {
    val worldPos = convertVec2(body.getPosition())
    val radius = convertScalar(.75f)
    val diameter = 2 * radius
    g.setColor(Color.YELLOW)
    g.fillOval((worldPos.x - radius).toInt, (worldPos.y - radius).toInt, diameter.toInt, diameter.toInt)

    val m = g.getTransform()
    g.rotate(body.getAngle())
    g.setColor(Color.red)
    g.drawOval(worldPos.x.toInt, worldPos.y.toInt, 1, 1)
    g.setTransform(m)
  }
  
  def executor = _executor
  def executor_= (value : BehaviorExecutor) = _executor = value
  
}