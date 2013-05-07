package edu.uccs.summers.actors

import akka.kernel.Bootable
import akka.actor.ActorSystem
import com.typesafe.config.ConfigFactory
import akka.actor.Actor
import akka.actor.Props
import akka.remote.RemoteLifeCycleEvent

class SimulationNode(portString : String) extends Bootable{
  val config = ConfigFactory.load("ComputeNode.conf").getConfig("computeNode")
  val port = ConfigFactory.parseString("akka.remote.netty.port=" + portString)
  val system = ActorSystem("ComputeNode", port.withFallback(config))

  system.eventStream.subscribe(system.actorOf(Props[NodeListener]), classOf[RemoteLifeCycleEvent])
  
  def startup(){}
  def shutdown(){}
}

object SimulationNode {
  def main(args: Array[String]) {
    val simNode = new SimulationNode(args(0))
    println("Simulation Compute Node Started - waiting for messages")
  }
}

class NodeListener extends Actor {
  println("Node lifecycle event listener online")
  def receive = {
    case d => println(d)
  }
}