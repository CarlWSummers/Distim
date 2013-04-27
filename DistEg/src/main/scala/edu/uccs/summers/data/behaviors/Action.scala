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

class RandomWalk extends Action(None, "") {
  override def perform(ctx : ExecutionContext) : Person = {
    val p = ctx.dereference("person").asInstanceOf[Person]
    val rnd = ctx.dereference("Random").asInstanceOf[Random]
    
    val newHeading = Math.atan2(p.body.getLinearVelocity().y, p.body.getLinearVelocity().x) + (15 * Math.PI / 180)
//    val newVelocity = new Vec2(Math.sin(newHeading).toFloat, Math.cos(newHeading).toFloat)
    val newVelocity = new Vec2(if(rnd.nextBoolean) -rnd.nextFloat else rnd.nextFloat, if(rnd.nextBoolean) -rnd.nextFloat else rnd.nextFloat)
    val newDynamics = PhysicalProperties(p.dynamics.position, newVelocity, p.dynamics.width, p.dynamics.mass)
    return Person(p.id, p.executor, newDynamics)
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


