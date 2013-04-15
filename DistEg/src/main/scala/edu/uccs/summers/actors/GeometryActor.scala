package edu.uccs.summers.actors

import scala.collection.mutable
import akka.actor.Actor
import akka.actor.ActorRef
import akka.actor.actorRef2Scala
import edu.uccs.summers.data.SimulationInitData
import edu.uccs.summers.data.Geometry
import edu.uccs.summers.data.behaviors.Behavior
import edu.uccs.summers.messages.AddSimulationListener
import edu.uccs.summers.messages.InitFailed
import edu.uccs.summers.messages.SimulationInitialize
import edu.uccs.summers.messages.SimulationInitializationResult
import edu.uccs.summers.messages.SimulationStepRequest
import edu.uccs.summers.messages.SimulationStepResult
import edu.uccs.summers.data.behaviors.BehaviorsParser
import edu.uccs.summers.data.behaviors.ParsingContext
import edu.uccs.summers.data.behaviors.RandomWalk
import edu.uccs.summers.data.behaviors.MoveDirect
import edu.uccs.summers.data.behaviors.Idle
import scala.io.Source
import edu.uccs.summers.messages.InitSuccessful
import akka.actor.Props
import edu.uccs.summers.data.Population
import edu.uccs.summers.data.Topography
import edu.uccs.summers.data.GeometryParser
import edu.uccs.summers.messages.InitFailed
import edu.uccs.summers.messages.InitFailed

class SimulationMaster extends Actor{
  
  private val listeners = scala.collection.mutable.Set[ActorRef]()
  private val behaviors = mutable.Map[String, Behavior]()
  private var initialized = false;
  
  private var geometry : Geometry = null
  private var popActor : ActorRef = null
  

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

    val parser = new BehaviorsParser(new ParsingContext)
    parser.bind("RandomWalk", new RandomWalk)
    parser.bind("Idle", new Idle)
    parser.bind("MoveDirect", new MoveDirect)
    
    parser.parseAll(parser.behaviorListing, Source.fromFile(initData.behaviorsFile).bufferedReader) match {
      case parser.Success(r, t) => {
        r.foreach(b => behaviors += (b.name -> b))
      }
      case e => 
        println("Failed to parse Behaviors File:" + e)
        return InitFailed("TEST")
    } 
    
    val geometryParser = new GeometryParser()
    
    geometryParser.parseAll(geometryParser.areaList , Source.fromFile(initData.geometryFile).bufferedReader) match {
      case geometryParser.Success(r, t) => {
        geometry = new Geometry(r)
      }
      case e => 
        println("Failed to parse Geometry File:" + e)
        return InitFailed("Failed to parse Geometry file")
      }
    
    val topo = new Topography("test/eg1/topography.txt")
    popActor = context.actorOf(Props(new Population(initData.populationFile, null, behaviors toMap)), name = "master")
    return InitSuccessful
  }
  
  def addSimulationListener(listener : ActorRef){
    listeners.add(listener)
  }
}