package edu.uccs.summers.actors

import scala.collection._
import scala.collection.mutable
import scala.io.Source
import scala.util.Random
import akka.actor.Actor
import akka.actor.ActorRef
import akka.actor.Props
import akka.actor.UntypedActorFactory
import akka.actor.actorRef2Scala
import akka.routing.RoundRobinRouter
import edu.uccs.summers.data.geometry.Geometry
import edu.uccs.summers.data.geometry.GeometryParser
import edu.uccs.summers.data.Person
import edu.uccs.summers.data.SimulationInitData
import edu.uccs.summers.data.behaviors.Behavior
import edu.uccs.summers.data.behaviors.BehaviorsParser
import edu.uccs.summers.data.behaviors.Idle
import edu.uccs.summers.data.behaviors.MoveDirect
import edu.uccs.summers.data.behaviors.ParsingContext
import edu.uccs.summers.data.behaviors.RandomWalk
import edu.uccs.summers.data.dto.geometry.{Geometry => GeometryDTO}
import edu.uccs.summers.data.geometry.Area
import edu.uccs.summers.data.population.PopulationArchetypeDescriptor
import edu.uccs.summers.data.population.PopulationFactory
import edu.uccs.summers.data.population.PopulationParser
import edu.uccs.summers.messages.AddSimulationListener
import edu.uccs.summers.messages.Compute
import edu.uccs.summers.messages.InitFailed
import edu.uccs.summers.messages.InitSuccessful
import edu.uccs.summers.messages.SimulationClear
import edu.uccs.summers.messages.SimulationInitializationResult
import edu.uccs.summers.messages.SimulationInitialize
import edu.uccs.summers.messages.SimulationStepExecutionComplete
import edu.uccs.summers.messages.SimulationStepRequest
import edu.uccs.summers.messages.SimulationStepResult
import edu.uccs.summers.messages.Compute
import edu.uccs.summers.messages.SimulationStart
import scala.concurrent.duration._
import akka.actor.Cancellable
import edu.uccs.summers.messages.SimulationStop
import edu.uccs.summers.messages.SimulationSpeed
import edu.uccs.summers.messages.RemoveSimulationListener
import edu.uccs.summers.data.behaviors.Follow

class SimulationMaster() extends Actor{
  
  private val listeners = scala.collection.mutable.Set[ActorRef]()
  private val behaviors = mutable.Map[String, Behavior]()
  private val popArchTypes = mutable.Map[String, PopulationArchetypeDescriptor]()
  
  private var initialized = false
  private var geometry : Geometry = null
  
  private var popExecs : ActorRef = null
  private var popAggregator : ActorRef = null
  
  var schedule : Cancellable = null
  var run = false
  var simulationSpeed = 1000 milliseconds
  
  def receive = {
    case SimulationInitialize(s) => {
      initSim(s) match {
        case InitSuccessful => {
          sender ! InitSuccessful
          listeners.foreach(_ ! SimulationClear)
          self ! SimulationStepRequest
        }
        case e : InitFailed => sender ! e
      } 
    }
    
    case SimulationStepExecutionComplete(geometry) => {
      import context.dispatcher
      listeners.foreach(_ ! SimulationStepResult(geometry))
      if (run) schedule = context.system.scheduler.scheduleOnce(simulationSpeed, self, SimulationStepRequest)
    }
    
    case SimulationStart => {
      import context.dispatcher
      run = true
      schedule = context.system.scheduler.scheduleOnce(0 seconds, self, SimulationStepRequest)
    }

    case SimulationStop => {
      if(schedule != null){
        run = false
        schedule.cancel
        schedule = null
      }
    }
    
    case SimulationStepRequest => {
      geometry.areas.foreach(area => popExecs ! Compute(popAggregator))
    }
    
    case SimulationSpeed(newDuration) => {
      simulationSpeed = newDuration
    }
    
    case AddSimulationListener(listener) => {
      addSimulationListener(listener)
    }
    
    case RemoveSimulationListener(listener) => {
      println("Simulation Master removed listener");
      removeSimulationListener(listener)
    }
  }
  
  def initSim(initData : SimulationInitData) : SimulationInitializationResult = {
    if(schedule != null){
      schedule.cancel
      schedule = null
    }
    behaviors.clear
    popArchTypes.clear
    geometry = null
    popExecs = null
    popAggregator = null
    
    val behaviorsParser = new BehaviorsParser(new ParsingContext)
    behaviorsParser.bind("RandomWalk", RandomWalk)
    behaviorsParser.bind("Idle", Idle)
    behaviorsParser.bind("MoveDirect", new MoveDirect)
    behaviorsParser.bind("Follow", Follow)
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

    val rnd = Random
    val geometryParser = new GeometryParser(popArchTypes, rnd)
    geometryParser.parseAll(geometryParser.geometry , Source.fromFile(initData.geometryFile).bufferedReader) match {
      case geometryParser.Success(r, t) => 
        geometry = r
      case e => 
        println("Failed to parse Geometry File:" + e)
        return InitFailed("Failed to parse Geometry file:" + e)
    }
    
    val execs = geometry.areas.map(area => context.actorOf(Props(new SimulationStepExecutor(area))))
    popExecs = context.actorOf(Props().withRouter(RoundRobinRouter(routees = execs)))
    popAggregator = context.actorOf(Props(new SimulationStepAggregator(execs.size)))
    
    initialized = true
    return InitSuccessful
  }
  
  def addSimulationListener(listener : ActorRef){
    listeners.add(listener)
  }
  
  def removeSimulationListener(listener : ActorRef){
    listeners.remove(listener)
  }
  
}