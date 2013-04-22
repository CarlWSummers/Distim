package edu.uccs.summers.data.behaviors

import edu.uccs.summers.data.Person
import scala.util.Random
import edu.uccs.summers.data.Topography
import edu.uccs.summers.data.Open
import edu.uccs.summers.data.Person
import edu.uccs.summers.data.TerrainType
import edu.uccs.summers.data.geometry.shapes.Vec2d

class Action (val parentAction : Option[Action], val body : String){
  
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
//    val newVelocity = Point(0, -5)
//    return Person(p.id, p.executor, p.position, p.velocity, p.currentArea)
    return p
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


