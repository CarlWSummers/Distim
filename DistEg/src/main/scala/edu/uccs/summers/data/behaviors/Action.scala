package edu.uccs.summers.data.behaviors

import edu.uccs.summers.data.Person
import edu.uccs.summers.data.Person
import org.jbox2d.common.Vec2
import edu.uccs.summers.data.population.PhysicalProperties
import scala.util.Random

class Action (val parentAction : Option[Action], val body : String) extends Serializable{
  
  def perform(ctx : ExecutionContext) : Person = {
    var person = ctx.dereference("person").get.asInstanceOf[Person]
    if(parentAction.isDefined) {
      return parentAction.get.perform(ctx)
    }
    return person
  }
}

case object RandomWalk extends Action(None, "") {
  val MAX_WANDER_ANGLE_CHANGE = math.toRadians(10)

  override def perform(ctx : ExecutionContext) : Person = {
    val p = ctx.dereference("person").get.asInstanceOf[Person]
    val rnd = ctx.dereference("Random").get.asInstanceOf[Random]
    
    def genRandomPoint() : Vec2 = {
      new Vec2(rnd.nextInt(10000) - 5000, rnd.nextInt(10000) - 5000)
    }
    
    val maxCount = 6 * 7 + 1
    var count = ctx.dereference("count", rnd.nextInt(maxCount))
    if(count > maxCount){
      ctx.bind("seekPoint", genRandomPoint)
      count = 0
    }
    val seekPoint = ctx.dereference("seekPoint", genRandomPoint)
    ctx.bind("count", count + 1)
    
//    val steerCircleDistance = 1f //Meters from person 
//    val steerCircleRadius = 15f //Meter radius
//    val wanderAngle = ctx.dereference("WanderAngle", 0f).asInstanceOf[Float]
//    
//    val circleCenter = p.body.getLinearVelocity().clone()
//    circleCenter.normalize()
//    circleCenter.mulLocal(steerCircleDistance)
//    
//    val displacement = new Vec2(0, 1)
//    displacement.mulLocal(steerCircleRadius)
//    def modifyDisplacement(displacement : Vec2, wanderAngle : Float){
//      val mag = displacement.length()
//      displacement.x = (math.cos(wanderAngle) * mag).toFloat
//      displacement.y = (math.sin(wanderAngle) * mag).toFloat
//    }
//    modifyDisplacement(displacement, wanderAngle)
//    
//    ctx.bind("WanderAngle",
//             (wanderAngle + (Random.nextDouble * MAX_WANDER_ANGLE_CHANGE - MAX_WANDER_ANGLE_CHANGE * .5)).toFloat)  
//
//    val seekPoint = circleCenter.add(displacement).add(p.body.getPosition())
    return new Seek(seekPoint).perform(ctx)
  }
}

case class Seek(dest : Vec2) extends Action(None, "") {
  val MAX_VELOCITY = 5f
  val MAX_CHANGE = .33f
  override def perform(ctx : ExecutionContext) : Person = {
    
    val p = ctx.dereference("person").get.asInstanceOf[Person]
    val maxChange = ctx.dereference("MAX_CHANGE", MAX_CHANGE)
    val maxVelocity = ctx.dereference("MAX_VELOCITY", MAX_VELOCITY)
    
    val desiredVector = dest.sub(p.body.getPosition())
    desiredVector.normalize()
    desiredVector.mulLocal(maxVelocity)
    
    val steeringForce = Utils.truncate(desiredVector.sub(p.body.getLinearVelocity()), maxChange)
    val newVelocity = Utils.truncate(p.body.getLinearVelocity().add(steeringForce), maxVelocity)
    val newDynamics = PhysicalProperties(p.dynamics.position, newVelocity, Utils.headingInDegrees(newVelocity).toFloat, p.dynamics.width, p.dynamics.mass)
    return Person(p.id, p.executor, newDynamics, p.archTypeName)
  }
}

case object Follow extends Action(None, "") {
  override def perform(ctx : ExecutionContext) : Person = {
    val p = ctx.dereference("person").get.asInstanceOf[Person]
    
    if(p.visualContacts.isEmpty) return RandomWalk.perform(ctx)
    val contactTuples = p.visualContacts.map(vc => (vc, p.body.getPosition().sub(vc.body.getPosition()).length()))
    val closest : (Person, Float) = contactTuples.reduceLeft((best, candidate) => {
      if(best._2 < candidate._2) best else candidate
    })
    
    val closestVelVector = closest._1.body.getLinearVelocity()
    val childCtx = ExecutionContext(ctx)
    childCtx.bind("MAX_VELOCITY", closestVelVector.length())
    
    val unitVel = closestVelVector.clone()
    unitVel.normalize()
    unitVel.x *= -1
    unitVel.y *= -1
    val seekPoint = closest._1.body.getPosition().add(unitVel)
    
    Seek(seekPoint).perform(ctx)
  }
}

case object Idle extends Action(None, ""){
  override def perform(ctx : ExecutionContext) : Person = {
    return ctx.dereference("person").get.asInstanceOf[Person]
  }
}

object Utils {
  
  def truncate(vector : Vec2, maxLength : Float) : Vec2 = {
    var s = maxLength / vector.length
    s = if(s < 1.0f) s else 1f
    new Vec2(vector.x * s, vector.y * s)
  }
  
  def headingInDegrees(velocityVector : Vec2) : Double = {
    -1 * (math.toDegrees(math.atan2(velocityVector.y, velocityVector.x)) - 90)
  }
  
  def headingInVec2(degrees : Float) : Vec2 = {
    val radians = math.toRadians(degrees)
    new Vec2(math.sin(radians).toFloat, math.cos(radians).toFloat)
  }
  
  def course(position : Vec2, target : Vec2) : Vec2 = {
    val v = target.sub(position)
    v.normalize
    v
  }
}
