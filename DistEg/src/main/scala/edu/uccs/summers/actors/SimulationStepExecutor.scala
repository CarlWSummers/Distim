package edu.uccs.summers.actors

import scala.collection.mutable
import scala.io.Source
import scala.util.Random

import akka.actor.Actor
import akka.actor.actorRef2Scala
import edu.uccs.summers.data.SimulationInitData
import edu.uccs.summers.data.behaviors.Behavior
import edu.uccs.summers.data.behaviors.BehaviorsParser
import edu.uccs.summers.data.behaviors.Follow
import edu.uccs.summers.data.behaviors.Idle
import edu.uccs.summers.data.behaviors.ParsingContext
import edu.uccs.summers.data.behaviors.RandomWalk
import edu.uccs.summers.data.geometry.Area
import edu.uccs.summers.data.geometry.Geometry
import edu.uccs.summers.data.geometry.GeometryParser
import edu.uccs.summers.data.population.PopulationArchetypeDescriptor
import edu.uccs.summers.data.population.PopulationParser
import edu.uccs.summers.messages.Compute
import edu.uccs.summers.messages.InitFailed
import edu.uccs.summers.messages.SimulationStepPartialResult

class SimulationStepExecutor(val areaName : String, val initData : SimulationInitData) extends Actor{
  
  private var initialized = false
  private var area : Area = null
  private var geometry : Geometry = null

  def receive = {
    case Compute(responseDest, incomingTransitions) => {
      if(!initialized){
        initialize()
      }
      
      val outgoingTransitions = area.update(incomingTransitions)
      responseDest ! SimulationStepPartialResult(area.translate, outgoingTransitions.map(_.translate).toSet)
    }
  }
  
  def initialize(){
    val behaviors = mutable.Map[String, Behavior]()
    val behaviorsParser = new BehaviorsParser(new ParsingContext)
    behaviorsParser.bind("RandomWalk", RandomWalk)
    behaviorsParser.bind("Idle", Idle)
    behaviorsParser.bind("Follow", Follow)
    behaviorsParser.parseAll(behaviorsParser.behaviorListing, Source.fromFile(initData.behaviorsFile).bufferedReader) match {
      case behaviorsParser.Success(r, t) => 
        r.foreach(b => behaviors += (b.name -> b))
      case e => 
    } 
    
    val popArchTypes = mutable.Map[String, PopulationArchetypeDescriptor]()
    val populationTypeParser = new PopulationParser(behaviors)
    populationTypeParser.parseAll(populationTypeParser.populationDescription, Source.fromFile(initData.populationFile).bufferedReader) match {
      case populationTypeParser.Success(r, t) => 
        r.foreach(p => popArchTypes.put(p.name, p))
      case e => 
    }

    val rnd = Random
    val geometryParser = new GeometryParser(popArchTypes, rnd)
    geometryParser.parseAll(geometryParser.geometry , Source.fromFile(initData.geometryFile).bufferedReader) match {
      case geometryParser.Success(r, t) => 
        geometry = r
      case e => 
    }

    area = geometry.areas.find(area => area.name.equals(areaName)).get
    area.initialize(popArchTypes)
    initialized = true
  }
}
