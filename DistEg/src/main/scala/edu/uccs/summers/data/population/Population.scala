package edu.uccs.summers.data.population

import scala.util.parsing.combinator.JavaTokenParsers
import edu.uccs.summers.data.behaviors.Behavior
import scala.collection._

case class PopulationArchetypeDescriptor(name : String, behavior : Behavior, props : Map[String, Any]) extends Serializable {
  
}

class PopulationParser(behaviors : mutable.Map[String, Behavior]) extends JavaTokenParsers {
  
  def populationDescription = ("Population" ~> "{" ~> rep(personType)) <~ "}"
  
  def personType = (("def" ~> name) <~ "{") ~ behaviorName ~ (properties <~ "}") ^^ {
    case name ~ behavior ~ properties => PopulationArchetypeDescriptor(name, behavior, properties)
  }
  
  def name = ident
  
  def behaviorName = "Behavior" ~> ":" ~> ident ^^ {
    case behaviorName => behaviors(behaviorName)
  }
  
  def properties = "Properties" ~> ":" ~> "{" ~> (rep(property) <~ "}") ^^ {
    case properties => properties.toMap
  }
  
  def property = ident ~ ":" ~ constant ^^ {
    case name ~ _ ~ value => (name -> value)
  }
  
  def constant = (
      stringLiteral ^^ {
        case str => str.replaceAll("\"", "")
      }
    | floatingPointNumber ^^ {
        case num => num.toDouble
      }
  )
}