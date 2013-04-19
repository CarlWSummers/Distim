package edu.uccs.summers.messages

import edu.uccs.summers.data.SimulationInitData
import edu.uccs.summers.data.Geometry
import akka.actor.Actor
import akka.actor.ActorRef
import edu.uccs.summers.data.Person

sealed trait SimulationMessage
case class SimulationInitialize(init : SimulationInitData) extends SimulationMessage
case object SimulationClear extends SimulationMessage

case object SimulationStepRequest extends SimulationMessage
case class SimulationStepResult(g : Geometry, pop : Set[Person]) extends SimulationMessage
case class SimulationStepPartialResult(partialPop : Set[Person]) extends SimulationMessage
case class SimulationStepExecutionComplete(population : Set[Person]) extends SimulationMessage

case class SimulationSpeed(speed : Int) extends SimulationMessage

sealed trait SimulationInitializationResult extends SimulationMessage
case object InitSuccessful extends SimulationInitializationResult
case class InitFailed(reason : String) extends SimulationInitializationResult

case class AddSimulationListener(listener : ActorRef)