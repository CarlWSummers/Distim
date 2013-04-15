package edu.uccs.summers.messages

import akka.actor.ActorRef

case class Forward(dest : ActorRef, message : Any)