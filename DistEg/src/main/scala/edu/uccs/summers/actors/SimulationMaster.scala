package edu.uccs.summers.actors

import scala.collection.mutable
import scala.concurrent.duration.DurationInt
import scala.io.Source
import scala.util.Random
import akka.actor.Actor
import akka.actor.ActorRef
import akka.actor.Cancellable
import akka.actor.Props
import akka.actor.actorRef2Scala
import akka.routing.RoundRobinRouter
import edu.uccs.summers.data.SimulationInitData
import edu.uccs.summers.data.behaviors.Behavior
import edu.uccs.summers.data.behaviors.BehaviorsParser
import edu.uccs.summers.data.behaviors.Follow
import edu.uccs.summers.data.behaviors.Idle
import edu.uccs.summers.data.behaviors.ParsingContext
import edu.uccs.summers.data.behaviors.RandomWalk
import edu.uccs.summers.data.geometry.Geometry
import edu.uccs.summers.data.geometry.GeometryParser
import edu.uccs.summers.data.population.PopulationArchetypeDescriptor
import edu.uccs.summers.data.population.PopulationParser
import edu.uccs.summers.messages.AddSimulationListener
import edu.uccs.summers.messages.Compute
import edu.uccs.summers.messages.InitFailed
import edu.uccs.summers.messages.InitSuccessful
import edu.uccs.summers.messages.RemoveSimulationListener
import edu.uccs.summers.messages.SimulationClear
import edu.uccs.summers.messages.SimulationInitializationResult
import edu.uccs.summers.messages.SimulationInitialize
import edu.uccs.summers.messages.SimulationSpeed
import edu.uccs.summers.messages.SimulationStart
import edu.uccs.summers.messages.SimulationStepExecutionComplete
import edu.uccs.summers.messages.SimulationStepRequest
import edu.uccs.summers.messages.SimulationStepResult
import edu.uccs.summers.messages.SimulationStop
import edu.uccs.summers.data.dto.geometry.TransitionEvent
import akka.actor.UntypedActorFactory
import akka.actor.UntypedActor
import akka.remote.routing.RemoteRouterConfig

class SimulationMaster() extends Actor{
  
  private val listeners = scala.collection.mutable.Set[ActorRef]()
  private val behaviors = mutable.Map[String, Behavior]()
  private val popArchTypes = mutable.Map[String, PopulationArchetypeDescriptor]()
  
  private var initialized = false
  private var geometry : Geometry = null
  private var latestTransitioners : Set[TransitionEvent] = Set[TransitionEvent]()
  
  private var popExecs : ActorRef = null
  private var popAggregator : ActorRef = null
  
  var schedule : Cancellable = null
  var run = false
  var simulationSpeed = 1000 milliseconds
  var elapsedTime = 0l
  var stepStart = 0l
  
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
    
    case SimulationStepExecutionComplete(geometry, transitioners) => {
      import context.dispatcher
      latestTransitioners = transitioners
      elapsedTime += System.currentTimeMillis() - stepStart
      listeners.foreach(_ ! SimulationStepResult(geometry, elapsedTime))
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
      stepStart = System.currentTimeMillis()
      geometry.areas.foreach(area => popExecs ! Compute(popAggregator, latestTransitioners))
    }
    
    case SimulationSpeed(newDuration) => {
      simulationSpeed = newDuration
    }
    
    case AddSimulationListener(listener) => {
      addSimulationListener(listener)
    }
    
    case RemoveSimulationListener(listener) => {
      removeSimulationListener(listener)
    }
  }
  
  def initSim(initData : SimulationInitData) : SimulationInitializationResult = {
    import scala.reflect.runtime.universe._
    val v = implicitly[TypeTag[Random]]

    if(schedule != null){
      schedule.cancel
      schedule = null
    }
    behaviors.clear
    popArchTypes.clear
    geometry = null
    popExecs = null
    popAggregator = null
    elapsedTime = 0l
    
    val behaviorsParser = new BehaviorsParser(new ParsingContext)
    behaviorsParser.bind("RandomWalk", RandomWalk)
    behaviorsParser.bind("Idle", Idle)
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
    
    import akka.actor.{ Address, AddressFromURIString }
    val addresses = Seq(
    AddressFromURIString("akka://ComputeNode@localhost:4114"))

    val factory = new ExecutorFactory(geometry.areas.map(_.name), initData)
    popExecs = context.actorOf(Props(factory).withRouter(RoundRobinRouter(nrOfInstances = geometry.areas.size)))
//    popExecs = context.actorOf(Props(factory).withRouter(RemoteRouterConfig(RoundRobinRouter(nrOfInstances = geometry.areas.size), addresses)))
    popAggregator = context.actorOf(Props(new SimulationStepAggregator(geometry.areas.size)))
    
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

class ExecutorFactory(var areaNames : List[String], initData : SimulationInitData) extends UntypedActorFactory{
  override def create() : SimulationStepExecutor = {
    val area = areaNames.head
    areaNames = areaNames.tail
    return new SimulationStepExecutor(area, initData)
  }
}