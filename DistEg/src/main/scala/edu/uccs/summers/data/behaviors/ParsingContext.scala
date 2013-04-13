package edu.uccs.summers.data.behaviors

import edu.uccs.summers.data.Person
import edu.uccs.summers.data.Population
import edu.uccs.summers.data.Topography
import scala.reflect.runtime.universe._
import scala.reflect.api._

class ParsingContext() {
  
  val classMap = Map[String, Class[_]](
    "person" -> classOf[Person],
    "people" -> classOf[Population],
    "environment" -> classOf[Topography]
  )
  
  def classForId(id : String) = classMap(id)
  
}