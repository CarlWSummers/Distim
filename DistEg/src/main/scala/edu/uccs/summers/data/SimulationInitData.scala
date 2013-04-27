package edu.uccs.summers.data

import java.io.File
import scala.util.parsing.combinator.JavaTokenParsers
import scala.collection.immutable.Nil
import scala.io.Source

case class SimulationInitData(files : List[File]) extends Serializable {
  private val parser = new SimulationInitDataParser
  val geometryFile = files.head
  val populationFile = files.tail.head
  val behaviorsFile = files.last
}

class SimulationInitDataParser extends JavaTokenParsers with Serializable {
  
  def parse(initDataFile : File) = {
    val files = this.parseAll(this.fileData, Source.fromFile(initDataFile).bufferedReader)

    files match {
      case this.Failure(e, t) => {
        println(t)
        throw new Exception(e)
      }
      case _ =>
    }
    files.get
  }
  
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
