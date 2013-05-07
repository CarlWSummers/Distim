package edu.uccs.summers.data

import java.awt.Color
import scala.annotation.tailrec
import scala.collection._
import scala.collection.mutable.ListBuffer
import scala.util.Random
import org.jbox2d.callbacks.QueryCallback
import org.jbox2d.collision.AABB
import org.jbox2d.collision.shapes.CircleShape
import org.jbox2d.collision.shapes.PolygonShape
import org.jbox2d.common.Vec2
import org.jbox2d.dynamics.Body
import org.jbox2d.dynamics.BodyDef
import org.jbox2d.dynamics.BodyType
import org.jbox2d.dynamics.Fixture
import org.jbox2d.dynamics.FixtureDef
import org.jbox2d.dynamics.World
import edu.uccs.summers.data.behaviors.BehaviorExecutor
import edu.uccs.summers.data.behaviors.ExecutionContext
import edu.uccs.summers.data.dto.HasDTO
import edu.uccs.summers.data.dto.population.{Person => PersonDTO}
import edu.uccs.summers.data.geometry.Area
import edu.uccs.summers.data.population.PhysicalProperties
import edu.uccs.summers.data.behaviors.ExecutionContext
import org.jbox2d.dynamics.Filter

case class Person(val id : String, private var _executor : BehaviorExecutor, dynamics : PhysicalProperties, archTypeName : String) extends Serializable with HasDTO[PersonDTO]{

  val VisualRange = 25
  val FOV = 170f
  val MaxVelocity = 4; //m/s
  
  var execContext : ExecutionContext = null
  var visualContacts = ListBuffer[Person]()
  var body : Body = null
  var sensor : PolygonShape = null
  
  def init(world : World, area : Area, useDynamics : Boolean){
    
    execContext = new ExecutionContext(null)
    populateExecutionContext(execContext)
    
    val bodyDef = new BodyDef
    bodyDef.`type` = BodyType.DYNAMIC
    bodyDef.allowSleep = false
    bodyDef.angularDamping = 1
    bodyDef.bullet = false
    bodyDef.position = if(useDynamics) dynamics.position else findSpawnPoint(area, world)
    bodyDef.linearVelocity = new Vec2
    body = world.createBody(bodyDef)
    body.setUserData(this)
    
    val bodyFixtureDef : FixtureDef = new FixtureDef
    bodyFixtureDef.density = 20
    val circle = new CircleShape
    circle.setRadius(.50f)
    bodyFixtureDef.shape = circle
    bodyFixtureDef.restitution = .05f
    bodyFixtureDef.filter = new Filter
    bodyFixtureDef.filter.categoryBits = Person.BODY_CATEGORY
    body.createFixture(bodyFixtureDef).setUserData(this)
    
    val visionFixtureDef = new FixtureDef
    visionFixtureDef.isSensor = true
    visionFixtureDef.filter = new Filter()
    visionFixtureDef.filter.categoryBits = Person.SENSOR_CATEGORY
    visionFixtureDef.filter.maskBits = Person.BODY_CATEGORY
    sensor = new PolygonShape
    val sensorPoints = ListBuffer[Vec2](new Vec2(0,0))
    val segments = 7
    for(i <- 0 until 7){
      val angle = math.toRadians((i / 6.0 * FOV) - (FOV / 2)) ;
      sensorPoints += new Vec2( (VisualRange * Math.cos(angle)).toFloat, (VisualRange * Math.sin(angle)).toFloat );
    }
    sensor.set(sensorPoints.toArray, sensorPoints.size)
    visionFixtureDef.shape = sensor
    body.createFixture(visionFixtureDef)
  }
  
  def terminate(world : World) = {
    world.destroyBody(body)
  }
  
  def populateExecutionContext(ctx : ExecutionContext){
    ctx.bind("rnd", Person.random)
    ctx.bind("Random", Person.random)
    ctx.bind("MAX_CHANGE", .33f)
    ctx.bind("MAX_VELOCITY", 5f)
    ctx.bind("person", this)
  }
  
  def addVisualContact(contact : Person) = {
    val pos = body.getPosition();
    
    visualContacts += contact
  }
  
  def removeVisualContact(contact : Person){
    visualContacts -= contact
  }
  
  def update(area : Area, pop : mutable.Set[Person]) = {
    val newDynamics = _executor.execute(area, pop.toSet, this).dynamics
    body.setLinearVelocity(newDynamics.velocity)
    body.setTransform(body.getPosition(), math.toRadians(-newDynamics.angle + 90).toFloat);
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
    visualContacts = visualContacts.filter(contact => {
      body.getPosition().sub(contact.body.getPosition()).abs().length() < VisualRange
    })
    new PersonDTO(id,
                  body.getPosition(), 
                  body.getLinearVelocity(), 
                  body.getAngle(), 
                  VisualRange, 
                  FOV, 
                  visualContacts.map(_.body.getPosition).toList, 
                  color,
                  archTypeName)
  }
}

object Person {
  val BODY_CATEGORY = 0x0001
  val SENSOR_CATEGORY = 0x0002
  val random = new Random
}