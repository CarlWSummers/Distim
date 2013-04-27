package edu.uccs.summers.data

import java.awt.Color
import java.awt.Graphics2D
import java.awt.geom.Ellipse2D
import java.awt.geom.Line2D

import org.jbox2d.callbacks.QueryCallback
import org.jbox2d.collision.AABB
import org.jbox2d.collision.shapes.CircleShape
import org.jbox2d.common.Vec2
import org.jbox2d.dynamics.Body
import org.jbox2d.dynamics.BodyDef
import org.jbox2d.dynamics.BodyType
import org.jbox2d.dynamics.Fixture
import org.jbox2d.dynamics.FixtureDef
import org.jbox2d.dynamics.World

import edu.uccs.summers.data.behaviors.BehaviorExecutor
import edu.uccs.summers.data.dto.HasDTO
import edu.uccs.summers.data.dto.population.{Person => PersonDTO}
import edu.uccs.summers.data.geometry.Area
import edu.uccs.summers.data.population.PhysicalProperties

case class Person(val id : String, private var _executor : BehaviorExecutor, dynamics : PhysicalProperties) extends Serializable with HasDTO[PersonDTO]{

  var body : Body = null
  
  def init(world : World, area : Area){
    val bodyDef = new BodyDef
    bodyDef.`type` = BodyType.DYNAMIC
    bodyDef.allowSleep = false
    bodyDef.angularDamping = 1
    bodyDef.bullet = false
    bodyDef.fixedRotation = false
    bodyDef.position = findSpawnPoint(area, world)
    bodyDef.linearVelocity = new Vec2
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
    body.setLinearVelocity(dynamics.velocity)
  }
  
  def executor = _executor
  def executor_= (value : BehaviorExecutor) = _executor = value
  
  def findSpawnPoint(area : Area, world : World) : Vec2 = {
    val potentialPoint = area.generateSpawnPoint
    val aabb = new AABB(new Vec2(potentialPoint.x - .75f, potentialPoint.y - .75f), new Vec2(potentialPoint.x + .75f, potentialPoint.y + .75f))
    var collision = false
    world.queryAABB(new QueryCallback{
     def reportFixture(fixture : Fixture) : Boolean = {
       collision = true
       return false
     }
    }, aabb)
    if(!collision) return potentialPoint else return findSpawnPoint(area, world)
  }

  def translate() : PersonDTO = {
    new PersonDTO(body.getPosition(), body.getLinearVelocity(), body.getAngle())
  }
}