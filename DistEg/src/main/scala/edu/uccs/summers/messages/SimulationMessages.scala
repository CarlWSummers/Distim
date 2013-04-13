package edu.uccs.summers.messages

import edu.uccs.summers.data.SimulationInitData

sealed trait SimulationMessage
case class Initialize(init : SimulationInitData) extends SimulationMessage

sealed trait SimulationInitializationResult extends SimulationMessage
case object InitSuccessful extends SimulationInitializationResult
case class InitFailed(reason : String) extends SimulationInitializationResult