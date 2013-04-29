package edu.uccs.summers.data.behaviors

import edu.uccs.summers.data.Person
import scala.util.Random
import edu.uccs.summers.data.Person
import org.jbox2d.common.Vec2
import edu.uccs.summers.data.population.PhysicalProperties

class Action (val parentAction : Option[Action], val body : String) extends Serializable{
  
  def perform(ctx : ExecutionContext) : Person = {
    var person = ctx.dereference("person").asInstanceOf[Person]
    if(parentAction.isDefined) {
      ctx.bind("person", person)
      return parentAction.get.perform(ctx)
    }
    return person
  }
}

case object RandomWalk extends Action(None, "") {
  val seekPoint = new Vec2(0,0)
  val maxVariation = 20
  val inhibitingCircleRadius = .5f
  
  override def perform(ctx : ExecutionContext) : Person = {
    val p = ctx.dereference("person").asInstanceOf[Person]
    val rnd = ctx.dereference("Random").asInstanceOf[Random]
    
    val vector = new Vec2(
      if(rnd.nextBoolean) rnd.nextInt() else -1 * rnd.nextInt(),
      if(rnd.nextBoolean) rnd.nextInt() else -1 * rnd.nextInt())
    
    vector.normalize()
    vector.mulLocal(maxVariation)
    
    seekPoint.addLocal(vector)
    
    val temp = seekPoint.sub(p.body.getPosition())
    temp.normalize()
    temp.mulLocal(inhibitingCircleRadius)
    seekPoint.addLocal(temp)
    
    return new Seek(seekPoint).perform(ctx)
  }
}

case class Seek(dest : Vec2) extends Action(None, "") {
  override def perform(ctx : ExecutionContext) : Person = {
    val p = ctx.dereference("person").asInstanceOf[Person]

    val currentHeading = Utils.headingInDegrees(p.body.getLinearVelocity())
    val courseToTarget = Utils.headingInDegrees(Utils.course(p.body.getPosition(), dest))
    val difference = courseToTarget - currentHeading
//    val sign = math.signum(difference)
    val newHeading = currentHeading + difference//(sign * math.min(90, math.abs(difference)))
    
    val newDynamics = PhysicalProperties(p.dynamics.position, Utils.headingInVec2(newHeading.toFloat).mulLocal(5f), newHeading.toFloat, p.dynamics.width, p.dynamics.mass)
    return Person(p.id, p.executor, newDynamics)
  }
}

case object Follow extends Action(None, "") {
  override def perform(ctx : ExecutionContext) : Person = {
    val p = ctx.dereference("person").asInstanceOf[Person]
    
    if(p.visualContacts.isEmpty) return RandomWalk.perform(ctx)
    val contactTuples = p.visualContacts.map(p => (p, p.body.getPosition().sub(p.visualContacts.head.body.getPosition()).length()))
    val closest : (Person, Float) = contactTuples.reduceLeft((best, candidate) => {
      if(best._2 < candidate._2) best else candidate
    })
    
    Seek(closest._1.body.getPosition()).perform(ctx)
  }
}

case object Idle extends Action(None, ""){
  override def perform(ctx : ExecutionContext) : Person = {
    return ctx.dereference("person").asInstanceOf[Person]
  }
}

class MoveDirect extends Action(None, ""){
  override def perform(ctx : ExecutionContext) : Person = {
    ctx.dereference("person").asInstanceOf[Person]
//    val pop = ctx.dereference("population").asInstanceOf[Population]
//    val terrain = ctx.dereference("terrain").asInstanceOf[Topography]
//    
//    val openMoves = pop.openMoves(person.position.x, person.position.y)
//    if(!openMoves.isEmpty){
//      val move = openMoves.map(a => terrain.getType(a._1, a._2)).reduce((a,b) => {
//        if(a.distance <= b.distance) a else b
//      })
//      return Person(person.id, person.executor, Point(move.x, move.y))
//    }else{
//      return person
//    }
  }
}

object Utils {
  def headingInDegrees(velocityVector : Vec2) : Double = {
    (360 + math.toDegrees(math.atan2(velocityVector.y, velocityVector.x)) - 90) % 360
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
