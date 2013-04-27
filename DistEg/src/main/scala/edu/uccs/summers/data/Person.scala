package edu.uccs.summers.data

import edu.uccs.summers.data.behaviors.BehaviorExecutor
import edu.uccs.summers.data.behaviors.Behavior
import edu.uccs.summers.data.behaviors.BehaviorExecutor
import scala.util.Random
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
import java.awt.geom.Ellipse2D
import java.awt.Shape
import java.awt.geom.AffineTransform
import org.jbox2d.callbacks.QueryCallback
import org.jbox2d.dynamics.Fixture
import org.jbox2d.collision.AABB
import java.awt.geom.Line2D
import java.io.ObjectOutputStream
import org.jbox2d.serialization.pb.PbSerializer
import java.io.ObjectInputStream
import org.jbox2d.serialization.pb.PbDeserializer

case class Person(val id : String, private var _executor : BehaviorExecutor, dynamics : PhysicalProperties) extends Serializable {

  @transient 
  var body : Body = null
  @transient
  var world : World = null
  
  def init(world : World, area : Area){
    this.world = world
    
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
  
  def draw(g : Graphics2D, convertScalar : Float => Float, convertVec2 : Vec2 => Vec2) = {
    val worldPos = convertVec2(body.getPosition())
    val radius = convertScalar(.75f)
    val diameter = 2 * radius
    val shape = new Ellipse2D.Float(-radius, -radius, diameter, diameter)
    
    val oldTransform = g.getTransform
    g.translate(worldPos.x, worldPos.y)
    g.setColor(Color.YELLOW)
    g.fill(shape);
    g.setColor(Color.GREEN)
    val velocity = convertVec2(body.getLinearVelocity())
    g.draw(new Line2D.Float(0, 0, velocity.x, velocity.y))
    g.setColor(Color.RED)
    g.rotate(body.getAngle().toDouble)
    g.draw(new Line2D.Float(0, 0, 0, -radius))
    g.setTransform(oldTransform)
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
       return false;
     }
    }, aabb)
    if(!collision) return potentialPoint else return findSpawnPoint(area, world)
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