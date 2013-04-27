package edu.uccs.summers.data.behaviors

import edu.uccs.summers.data.Person
import scala.reflect.runtime.universe._
import scala.reflect.api._

class ParsingContext() extends Serializable {
  
  val classMap = Map[String, Class[_]](
    "person" -> classOf[Person],
    "people" -> classOf[Set[Person]]
  )
  
  def classForId(id : String) = classMap(id)
  
}