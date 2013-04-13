package edu.uccs.summers.actors

import akka.actor.Actor
import edu.uccs.summers.messages.PiApproximation
import edu.uccs.summers.messages.Initialize
import edu.uccs.summers.data.SimulationInitData
import edu.uccs.summers.messages.InitFailed
import edu.uccs.summers.messages.SimulationInitializationResult

class SimulationMaster extends Actor{
  def receive = {
    case Initialize(s) => {
      val result = initSim(s)
      sender ! result
    }
  }
  
  def initSim(initData : SimulationInitData) : SimulationInitializationResult = {
    InitFailed("TEST")
  }
}