package edu.uccs.summers.messages

import edu.uccs.summers.data.SimulationInitData
import edu.uccs.summers.data.Geometry
import akka.actor.Actor
import akka.actor.ActorRef

sealed trait SimulationMessage
case class SimulationInitialize(init : SimulationInitData) extends SimulationMessage
case class SimulationStepResult(g : Geometry) extends SimulationMessage
case object SimulationStepRequest extends SimulationMessage
case class SimulationSpeed(speed : Int) extends SimulationMessage

sealed trait SimulationInitializationResult extends SimulationMessage
case object InitSuccessful extends SimulationInitializationResult
case class InitFailed(reason : String) extends SimulationInitializationResult

case class AddSimulationListener(listener : ActorRef)