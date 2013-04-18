package edu.uccs.summers.messages

import akka.actor.ActorRef
import edu.uccs.summers.data.Person
import edu.uccs.summers.data.geometry.Area

sealed trait PopulationMessage

case class Compute(area : Area, pop : Set[Person], resultDest : ActorRef)