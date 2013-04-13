package edu.uccs.summers.data.behaviors

import edu.uccs.summers.data.Person
import scala.util.Random
import edu.uccs.summers.data.Position
import edu.uccs.summers.data.Topography
import edu.uccs.summers.data.Open
import edu.uccs.summers.data.Population
import edu.uccs.summers.data.Person
import edu.uccs.summers.data.TerrainType

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
    val pop = ctx.dereference("population").asInstanceOf[Population]
    
    val move = Random.shuffle(pop.openMoves(p.position.x, p.position.y)).take(1).headOption
    if(move.isDefined)
      return Person(p.id, p.name, p.male, p.executor, Position(move.get._1, move.get._2))
    return p
  }
}

class Idle extends Action(None, ""){
  override def perform(ctx : ExecutionContext) : Person = {
    return ctx.dereference("person").asInstanceOf[Person]
  }
}

class MoveDirect extends Action(None, ""){
  override def perform(ctx : ExecutionContext) : Person = {
    val person = ctx.dereference("person").asInstanceOf[Person]
    val pop = ctx.dereference("population").asInstanceOf[Population]
    val terrain = ctx.dereference("terrain").asInstanceOf[Topography]
    
    val openMoves = pop.openMoves(person.position.x, person.position.y)
    if(!openMoves.isEmpty){
      val move = openMoves.map(a => terrain.getType(a._1, a._2)).reduce((a,b) => {
        if(a.distance <= b.distance) a else b
      })
      return Person(person.id, person.name, person.male, person.executor, Position(move.x, move.y))
    }else{
      return person
    }
  }
}


