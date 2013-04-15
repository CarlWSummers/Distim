package edu.uccs.summers.data

import scala.util.parsing.combinator.JavaTokenParsers
import scala.io.Source
import edu.uccs.summers.data.geometry.shapes.Rectangle
import edu.uccs.summers.data.geometry.shapes.Point
import edu.uccs.summers.data.geometry.shapes.Circle
import edu.uccs.summers.data.geometry.shapes.Shape
import edu.uccs.summers.data.geometry.shapes.Polygon
import edu.uccs.summers.data.geometry.AreaTransition
import edu.uccs.summers.data.geometry.Area

class Geometry(val areas : List[Area]) {

//  val parser = new GeometryParser()

}

class GeometryParser extends JavaTokenParsers {

  def areaList = rep(areaDescription)
  
  def areaDescription = ("Area" ~> areaName) ~ ("{" ~> objectList) <~ "}" ^^ {
    case name ~ entityList => Area(name, entityList)
  }
  
  def areaName = ident
  
  def objectList = rep(objectDescription)
  
  def objectDescription = objectType
  
  def objectType = (
      ("Wall" ~> "{" ~> wallProperties) <~ "}"
    | ("Transition" ~> "{" ~> transitionProperties) <~ "}"
    | ("Exit" ~> "{" ~> exitProperties) <~ "}"
  )
  
  def exitProperties = shapeDef ^^ {
    case shape => new edu.uccs.summers.data.geometry.Exit(shape)
  }
  
  def wallProperties = shapeDef ^^ {
    case shape => new edu.uccs.summers.data.geometry.Wall(shape)
  }
  
  def transitionProperties = ident ~ shapeDef ~ transitionDef ^^ {
    case name ~ shape ~ destArea => AreaTransition(name, shape, destArea._1, destArea._2)
  }
  
  def transitionDef = ("transition" ~> "to" ~> ident) ~ ident ^^ {
    case area ~ transitionDef => (area, transitionDef)
  }
  
  def shapeDef = (
      "polygon" ~> polyParameters
    | "rectangle" ~> rectangleParameters
    | "circle" ~> circleParameters
  )
  
  def rectangleParameters = (
      rectanglePointPoint
    | rectanglePointDimensions
  )
  
  def circleParameters : Parser[Circle] = ("center" ~> ":" ~> pointDesc) ~ ("radius" ~> ":" ~> wholeNumber) ^^ {
    case center ~ radius => Circle(center, radius.toInt)
  }
  
  def rectanglePointPoint = ("upper" ~ "left" ~> pointDesc) ~ ("to" ~ "lower" ~ "right" ~> pointDesc) ^^ {
    case ul ~ lr => Rectangle(ul, lr)
  }
  
  def rectanglePointDimensions = 
    ("starting" ~> "at" ~> pointDesc) ~ 
    ("with" ~> "width" ~> wholeNumber) ~ 
    ("and" ~> "height" ~> wholeNumber) ^^ 
    {
      case  point ~ width ~ height => Rectangle(point, width.toInt, height.toInt)
    }
  
  def polyParameters = pointDesc ~ ("," ~> pointDesc) ~ ("," ~> pointList) ^^ {
    case first ~ second ~ rest => Polygon(first :: second :: rest)
  }
  
  def pointDesc = ("(" ~> wholeNumber) ~ ("," ~> wholeNumber) <~ ")" ^^ {
    case x ~ y => Point(x.toInt, y.toInt)
  }
  
  def pointList : Parser[List[Point]] = (
      pointDesc ~ ("," ~> pointList) ^^ {
        case point ~ list => point :: list
      }
    | pointDesc ^^ {
        case point => List(point)
      }
  )
}