package edu.uccs.summers.data.behaviors

import edu.uccs.summers.data.behaviors.interpreter.Expression

case class Transition (val destinationState : String, val pred : Expression){
  def apply(ctx : ExecutionContext) = pred(ctx)
  def transitionTo = destinationState
}