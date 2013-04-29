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
import org.jbox2d.collision.shapes.PolygonShape
import scala.collection.mutable.ListBuffer
import scala.annotation.tailrec
import edu.uccs.summers.data.behaviors.ExecutionContext

case class Person(val id : String, private var _executor : BehaviorExecutor, dynamics : PhysicalProperties) extends Serializable with HasDTO[PersonDTO]{

  val VisualRange = 10
  val FOV = 260f
  val MaxVelocity = 4; //m/s
  val execContext = new ExecutionContext(null)
  
  var visualContacts = ListBuffer[Person]()
  var body : Body = null
  
  def init(world : World, area : Area){
    val bodyDef = new BodyDef
    bodyDef.`type` = BodyType.DYNAMIC
    bodyDef.allowSleep = false
    bodyDef.angularDamping = 1
    bodyDef.fixedRotation = true
    bodyDef.bullet = false
    bodyDef.position = findSpawnPoint(area, world)
    bodyDef.linearVelocity = new Vec2
    body = world.createBody(bodyDef)
    body.setUserData(this)
    
    val bodyFixtureDef : FixtureDef = new FixtureDef
    bodyFixtureDef.density = 20
    val circle = new CircleShape
    circle.setRadius(.50f)
    bodyFixtureDef.shape = circle
    bodyFixtureDef.restitution = .05f
    body.createFixture(bodyFixtureDef).setUserData(this)
    
    val visionFixtureDef = new FixtureDef
    visionFixtureDef.isSensor = true
    val sensor = new PolygonShape
    val sensorPoints = ListBuffer[Vec2](new Vec2(0,0))
    val segments = 7
    0 to segments-1 foreach (i => {
      val angle = math.toRadians((i / (segments-1).toFloat * FOV) + ((180 - FOV) / 2))
      val x = VisualRange * math.cos(angle).toFloat
      val y = VisualRange * math.sin(angle).toFloat 
      sensorPoints += new Vec2(x, y);
    })
    sensor.set(sensorPoints.toArray, sensorPoints.size)
    visionFixtureDef.shape = sensor
    body.createFixture(visionFixtureDef)
    
  }
  
  def addVisualContact(contact : Person) = {
    visualContacts += contact
  }
  
  def removeVisualContact(contact : Person){
    visualContacts -= contact
  }
  
  def update(area : Area, pop : Set[Person]) = {
    val newDynamics = _executor.execute(area, pop, this).dynamics
    body.setLinearVelocity(newDynamics.velocity)
    body.setTransform(body.getPosition(), math.toRadians(newDynamics.angle).toFloat);
    body.setAngularVelocity(0)
  }
  
  def executor = _executor
  def executor_= (value : BehaviorExecutor) = _executor = value
  
  var _color = Color.YELLOW
  def color = _color
  def color_= (value : Color) = _color = value
  
  @tailrec
  final def findSpawnPoint(area : Area, world : World) : Vec2 = {
    val potentialPoint = area.generateSpawnPoint
    val aabb = new AABB(new Vec2(potentialPoint.x - .50f, potentialPoint.y - .50f), new Vec2(potentialPoint.x + .50f, potentialPoint.y + .50f))
    var collision = false
    world.queryAABB(new QueryCallback{
     def reportFixture(fixture : Fixture) : Boolean = {
       collision = fixture.isSensor()
       return collision
     }
    }, aabb)
    if(!collision) return potentialPoint else return findSpawnPoint(area, world)
  }

  def translate() : PersonDTO = {
    new PersonDTO(body.getPosition(), body.getLinearVelocity(), body.getAngle(), VisualRange, FOV, visualContacts.map(_.body.getPosition).toList, color)
  }
}