package edu.uccs.summers.data.behaviors

import edu.uccs.summers.data.behaviors.interpreter.Expression

case class Transition (val destinationState : String, val pred : Expression) extends Serializable{
  def apply(ctx : ExecutionContext) : Boolean = pred(ctx).asInstanceOf[Boolean]
  def transitionTo = destinationState
}