package edu.uccs.summers.actors

import scala.collection.mutable
import com.typesafe.config.ConfigFactory
import akka.actor.Actor
import akka.actor.ActorRef
import akka.actor.ActorSystem
import akka.actor.Props
import akka.actor.actorRef2Scala
import akka.kernel.Bootable
import edu.uccs.summers.messages.SimulationCreate
import edu.uccs.summers.messages.SimulationEnd
import edu.uccs.summers.messages.SimulationListing
import edu.uccs.summers.messages.SimulationLookup
import edu.uccs.summers.messages.SimulationReference
import edu.uccs.summers.messages.SimulationListingReponse

class SimulationServer extends Bootable {
  
  val system = ActorSystem("SimulationCoordination", ConfigFactory.load().getConfig("server"))
  val actor = system.actorOf(Props[SimulationCoordinator], "coordinator")
  
  def startup(){}
  def shutdown(){}
}

class SimulationCoordinator extends Actor {
  
  val simulations = mutable.Map[String, ActorRef]()
  
  def receive = {
    case SimulationCreate(name) => {
      if(simulations.contains(name)){
        sender ! SimulationReference(simulations(name))
      }else{
        val simMaster = context.actorOf(Props[SimulationMaster])
        simulations += name -> simMaster
        sender ! SimulationReference(simMaster)
      }
    }
    
    case SimulationListing => {
      sender ! SimulationListingReponse(simulations.keySet.toSet)
    }

    case SimulationLookup(name) => {
      sender ! SimulationReference(simulations(name))
    }
    
    case SimulationEnd(name) => {
      // TODO
    }
  }
}


object SimulationServer {
  def main(args: Array[String]) {
    new SimulationServer
    println("Simulation Coordinator Started - waiting for messages")
  }
}