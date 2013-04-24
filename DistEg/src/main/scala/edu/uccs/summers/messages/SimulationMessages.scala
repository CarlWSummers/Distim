package edu.uccs.summers.messages

import edu.uccs.summers.data.SimulationInitData
import edu.uccs.summers.data.geometry.Geometry
import akka.actor.Actor
import akka.actor.ActorRef
import edu.uccs.summers.data.Person
import edu.uccs.summers.data.geometry.Area
import edu.uccs.summers.data.geometry.Geometry

sealed trait SimulationMessage
case class SimulationInitialize(init : SimulationInitData) extends SimulationMessage
case object SimulationClear extends SimulationMessage

case object SimulationStepRequest extends SimulationMessage
case object SimulationStart extends SimulationMessage
case object SimulationStop extends SimulationMessage

case class SimulationStepResult(g : Geometry) extends SimulationMessage
case class SimulationStepPartialResult(newArea : Area) extends SimulationMessage
case class SimulationStepExecutionComplete(geometry : Geometry) extends SimulationMessage
case class Compute(area : Area, resultDest : ActorRef) extends SimulationMessage

case class SimulationSpeed(speed : Int) extends SimulationMessage

sealed trait SimulationInitializationResult extends SimulationMessage
case object InitSuccessful extends SimulationInitializationResult
case class InitFailed(reason : String) extends SimulationInitializationResult

case class AddSimulationListener(listener : ActorRef)