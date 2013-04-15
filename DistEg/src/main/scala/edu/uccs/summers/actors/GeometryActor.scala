package edu.uccs.summers.actors

import scala.collection.mutable
import scala.io.Source
import akka.actor.Actor
import akka.actor.ActorRef
import akka.actor.Props
import akka.actor.actorRef2Scala
import edu.uccs.summers.data.Geometry
import edu.uccs.summers.data.GeometryParser
import edu.uccs.summers.data.Population
import edu.uccs.summers.data.SimulationInitData
import edu.uccs.summers.data.behaviors.Behavior
import edu.uccs.summers.data.behaviors.BehaviorsParser
import edu.uccs.summers.data.behaviors.Idle
import edu.uccs.summers.data.behaviors.MoveDirect
import edu.uccs.summers.data.behaviors.ParsingContext
import edu.uccs.summers.data.behaviors.RandomWalk
import edu.uccs.summers.messages.AddSimulationListener
import edu.uccs.summers.messages.InitFailed
import edu.uccs.summers.messages.InitSuccessful
import edu.uccs.summers.messages.SimulationInitializationResult
import edu.uccs.summers.messages.SimulationInitialize
import edu.uccs.summers.messages.SimulationStepRequest
import edu.uccs.summers.messages.SimulationStepResult
import edu.uccs.summers.data.population.PopulationParser
import edu.uccs.summers.data.population.PopulationArchetypeDescriptor

class SimulationMaster extends Actor{
  
  private val listeners = scala.collection.mutable.Set[ActorRef]()
  private val behaviors = mutable.Map[String, Behavior]()
  private val popArchTypes = mutable.Map[String, PopulationArchetypeDescriptor]()
  
  private var initialized = false;
  
  private var geometry : Geometry = null
  

  def receive = {
    case SimulationInitialize(s) => {
      initSim(s) match {
        case InitSuccessful => {
          sender ! InitSuccessful
          listeners.foreach(_ ! SimulationStepResult(geometry))
        }
        case e : InitFailed => sender ! e
      }
    }
    case SimulationStepRequest => {
      listeners.foreach(_ ! SimulationStepResult(geometry))
    }
    
    case AddSimulationListener(listener) => {
      addSimulationListener(listener)
    }
  }
  
  def initSim(initData : SimulationInitData) : SimulationInitializationResult = {
    initialized = true;

    val behaviorsParser = new BehaviorsParser(new ParsingContext)
    behaviorsParser.bind("RandomWalk", new RandomWalk)
    behaviorsParser.bind("Idle", new Idle)
    behaviorsParser.bind("MoveDirect", new MoveDirect)
    behaviorsParser.parseAll(behaviorsParser.behaviorListing, Source.fromFile(initData.behaviorsFile).bufferedReader) match {
      case behaviorsParser.Success(r, t) => 
        r.foreach(b => behaviors += (b.name -> b))
        
      case e => 
        return InitFailed("Failed to parse Behaviors File:" + e)
    } 
    
    val populationTypeParser = new PopulationParser(behaviors)
    populationTypeParser.parseAll(populationTypeParser.populationDescription, Source.fromFile(initData.populationFile).bufferedReader) match {
      case populationTypeParser.Success(r, t) => 
        r.foreach(p => popArchTypes.put(p.name, p))
      case e => 
        return InitFailed("Failed to parse Population file:" + e)
    }

    val geometryParser = new GeometryParser(popArchTypes)
    geometryParser.parseAll(geometryParser.areaList , Source.fromFile(initData.geometryFile).bufferedReader) match {
      case geometryParser.Success(r, t) => 
        geometry = new Geometry(r)
      case e => 
        println("Failed to parse Geometry File:" + e)
        return InitFailed("Failed to parse Geometry file:" + e)
    }
    
    
//    popActor = context.actorOf(Props(new Population(initData.populationFile, null, behaviors toMap)), name = "master")
    return InitSuccessful
  }
  
  def addSimulationListener(listener : ActorRef){
    listeners.add(listener)
  }
}