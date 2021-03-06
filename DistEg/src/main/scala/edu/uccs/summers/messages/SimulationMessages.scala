package edu.uccs.summers.messages

import scala.concurrent.duration.FiniteDuration
import akka.actor.ActorRef
import edu.uccs.summers.data.SimulationInitData
import edu.uccs.summers.data.dto.geometry.{Area => AreaDTO}
import edu.uccs.summers.data.dto.geometry.{Geometry => GeometryDTO}
import edu.uccs.summers.data.geometry.Area
import edu.uccs.summers.data.geometry.Geometry
import edu.uccs.summers.data.dto.population.Person
import edu.uccs.summers.data.dto.geometry.TransitionEvent
import edu.uccs.summers.data.dto.geometry.TransitionEvent

sealed trait SimulationMessage extends Serializable
case class SimulationCreate(simName : String) extends SimulationMessage
case class SimulationReference(simulationMaster : ActorRef) extends SimulationMessage
case class SimulationEnd(simName : String) extends SimulationMessage

case object SimulationListing extends SimulationMessage
case class SimulationListingReponse(values : Set[String]) extends SimulationMessage
case class SimulationLookup(name : String) extends SimulationMessage

case class SimulationInitialize(init : SimulationInitData) extends SimulationMessage
case object SimulationClear extends SimulationMessage

case object SimulationStepRequest extends SimulationMessage
case object SimulationStart extends SimulationMessage
case class  SimulationExecutorInit(val areaName : String, val initData : SimulationInitData) extends SimulationMessage
case object SimulationStop extends SimulationMessage

case class SimulationStepResult(g : GeometryDTO, elapsedTime : Long) extends SimulationMessage
case class SimulationStepPartialResult(newArea : AreaDTO, transitioners : Set[TransitionEvent]) extends SimulationMessage
case class SimulationStepExecutionComplete(geometry : GeometryDTO, transitions : Set[TransitionEvent]) extends SimulationMessage
case class Compute(resultDest : ActorRef, transitions : Set[TransitionEvent]) extends SimulationMessage

case class SimulationSpeed(speed : FiniteDuration) extends SimulationMessage

sealed trait SimulationInitializationResult extends SimulationMessage
case object InitSuccessful extends SimulationInitializationResult
case class InitFailed(reason : String) extends SimulationInitializationResult

case class AddSimulationListener(listener : ActorRef)
case class RemoveSimulationListener(listener : ActorRef)