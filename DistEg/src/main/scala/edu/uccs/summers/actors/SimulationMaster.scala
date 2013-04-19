package edu.uccs.summers.actors

import scala.collection._
import scala.collection.mutable
import scala.io.Source
import scala.util.Random

import akka.actor.Actor
import akka.actor.ActorRef
import akka.actor.ActorSystem
import akka.actor.Props
import akka.actor.UntypedActorFactory
import akka.actor.actorRef2Scala
import akka.routing.RoundRobinRouter
import edu.uccs.summers.data.Geometry
import edu.uccs.summers.data.GeometryParser
import edu.uccs.summers.data.Person
import edu.uccs.summers.data.SimulationInitData
import edu.uccs.summers.data.behaviors.Behavior
import edu.uccs.summers.data.behaviors.BehaviorsParser
import edu.uccs.summers.data.behaviors.Idle
import edu.uccs.summers.data.behaviors.MoveDirect
import edu.uccs.summers.data.behaviors.ParsingContext
import edu.uccs.summers.data.behaviors.RandomWalk
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

class SimulationMaster(system : ActorSystem) extends Actor{
  
  private val listeners = scala.collection.mutable.Set[ActorRef]()
  private val behaviors = mutable.Map[String, Behavior]()
  private val popArchTypes = mutable.Map[String, PopulationArchetypeDescriptor]()
  
  private var initialized = false;
  private var geometry : Geometry = null
  private var latestPopulation : Set[Person] = Set()
  
  private var popExecs : ActorRef = null
  private var popAggregator : ActorRef = null
  
  def receive = {
    case SimulationInitialize(s) => {
      initSim(s) match {
        case InitSuccessful => {
          sender ! InitSuccessful
          listeners.foreach(_ ! SimulationClear)
          listeners.foreach(_ ! SimulationStepResult(geometry, latestPopulation.toSet))
        }
        case e : InitFailed => sender ! e
      } 
    }
    
    case SimulationStepExecutionComplete(newPop) => {
      latestPopulation = newPop
      listeners.foreach(_ ! SimulationStepResult(geometry, newPop))
    }
    
    case SimulationStepRequest => {
      val popByArea = latestPopulation.foldLeft(mutable.Map[Area, mutable.Set[Person]]())((areaToPeopleMap, person) => {
        if(!areaToPeopleMap.contains(person.currentArea)){
          areaToPeopleMap.put(person.currentArea, mutable.Set())
        }
        areaToPeopleMap(person.currentArea) += person
        areaToPeopleMap
      })
      popByArea.foreach((tuple) => {
        popExecs ! Compute(tuple._1, tuple._2.toSet, popAggregator)
      })
    }
    
    case AddSimulationListener(listener) => {
      addSimulationListener(listener)
    }
  }
  
  def initSim(initData : SimulationInitData) : SimulationInitializationResult = {
    behaviors.clear
    popArchTypes.clear
    geometry = null
    latestPopulation = Set()
    popExecs = null
    popAggregator = null
    
    val behaviorsParser = new BehaviorsParser(new ParsingContext)
    behaviorsParser.bind("RandomWalk", new RandomWalk)
    behaviorsParser.bind("Idle", Idle)
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

    val rnd = Random
    geometry.areas.foreach(area => {
      val maxPop = area.popParams.maxSize
      val minPop = area.popParams.minSize
      val count = if(maxPop == minPop) minPop else rnd.nextInt(maxPop - minPop) + minPop
      val fixmeType = area.popParams.popTypes.head
      for(i <- 0 to count - 1){
        latestPopulation += PopulationFactory.createPerson(area.name + "_" + i, fixmeType, area)
      }
    })
    
    popExecs = context.actorOf(Props[SimulationStepExecutor].withRouter(RoundRobinRouter(geometry.areas.size)))
    popAggregator = context.actorOf(Props(new SimulationStepAggregator(geometry.areas.size)))
    
    initialized = true;
    return InitSuccessful
  }
  
  def addSimulationListener(listener : ActorRef){
    listeners.add(listener)
  }
}