package edu.uccs.summers.data

import java.io.File
import scala.util.parsing.combinator.JavaTokenParsers
import scala.collection.immutable.Nil
import scala.io.Source

case class SimulationInitData(initDataFile : File){
  private val parser = new SimulationInitDataParser
  private val files = parser.parseAll(parser.fileData, Source.fromFile(initDataFile).bufferedReader)

  files match {
    case parser.Failure(e,t) => {
      println(t)
      throw new Exception(e)
    }
    case _ =>
  }
  val geometryFile = files.get.head
  val populationFile = files.get.tail.head
  val behaviorsFile = files.get.last
}

class SimulationInitDataParser extends JavaTokenParsers {
  def fileData = geometryFileDef ~ populationFileDef ~ behaviorsFileDef ^^ {
    case geo ~ pop ~ behavior => geo :: pop :: behavior :: Nil
  }
  
  def geometryFileDef = "geometry" ~> ":" ~> fileName ^^ {
    case filename => filename
  }
  
  def populationFileDef = "population" ~> ":" ~> fileName ^^ {
    case filename => filename
  }

  def behaviorsFileDef = "behaviors" ~> ":" ~> fileName ^^ {
    case filename => filename
  }
  
  def fileName = stringLiteral ^^ {
    case filename => new File(filename.replace("\"", ""))
  }
}
