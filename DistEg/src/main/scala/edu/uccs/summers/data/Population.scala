package edu.uccs.summers.data

import scala.util.parsing.json.JSON
import scala.io.Source
import java.io.File
import akka.actor.Actor
import edu.uccs.summers.messages.PopulationRequest
import edu.uccs.summers.messages.PopulationResponse
import scala.util.Random
import scala.collection._
import edu.uccs.summers.data.behaviors.Behavior
import edu.uccs.summers.data.geometry.shapes.Point

class CC[T] { 
  def unapply(a:Any):Option[T] = 
    Some(a.asInstanceOf[T]) 
}

object M extends CC[Map[String, Any]]
object P extends CC[List[Any]]
object Name extends CC[String]
object ID extends CC[String]
object Behaviors extends CC[String]
object Male extends CC[Boolean]
object Xs extends CC[Double]
object Ys extends CC[Double]

case class PersonHolder(id :String, name : String, male : Boolean, behaviorName : String, pos : Position)

class Population(file : File, terrain : Topography, behaviors : Map[String, Behavior]) extends Actor{
  val sourceFile = Source.fromFile(file)
  val jsonString = sourceFile.mkString
  sourceFile.close
  
  var map = Map[String, Person]()
  for(p <- for {
    Some(M(map)) <- List(JSON.parseFull(jsonString))
    P(persons) = map("persons")
    M(person) <- persons
    ID(id) = person("id")
    Behaviors(behavior) = person("behavior")
    Name(name) = person("name")
    Male(male) = person("male")
    Xs(x) = person("x")
    Ys(y) = person("y")
    
  } yield PersonHolder(id, name, male, behavior, Position(x.toInt, y.toInt))){
    val person = Person(p.id, null, Point(p.pos.x, p.pos.y))
    person.executor = behaviors.get(p.behaviorName).get.executor(person)
    map += (p.id -> person)
  }
  
  def getByID(id : String) : Person = {
    map(id)
  }
  
  def occupied(x : Int, y : Int) = {
    at(x, y).get.size > 0
  }
  
  def at(x : Int, y : Int) : Option[List[Person]] = {
    Some(map.filter((p : (String, Person)) => {
      p._2.position.x == x && p._2.position.y == y 
    }).map((p: (String, Person)) => p._2).toList)
  }
  
  def openMoves(x : Int, y : Int) : Iterable[(Int, Int)]= {
    var moves = mutable.Seq
    for(i <- x-1 to x+1;
        j <- y-1 to y+1
        if(terrain.isNotBlocked(i, j) && !occupied(i, j))) yield (i, j)
  }
  
  def receive = {
    case PopulationRequest => {
      for(p <- map.values){
        val updatedPerson = p.update(terrain, this)
        if(!terrain.getType(p.position.x, p.position.y).isInstanceOf[Exit])
          map += (p.id -> updatedPerson) 
        else
          map -= p.id
      }
      sender ! PopulationResponse(this)
    }
  }
  
  def size = map.size
}

case class Position(x : Int, y : Int)
