package edu.uccs.summers.data.behaviors

import scala.util.Random

import edu.uccs.summers.data.Person
import edu.uccs.summers.data.geometry.Area
import edu.uccs.summers.data.geometry.Geometry


class Behavior (val name : String, val states : List[State]) extends Serializable{
  val nullState = new State("", false, null, null)
  def executor (p : Person) = new BehaviorExecutor(this)
  def stateOf(s : String, default : State) : State = {
    states.find(_.name == s).getOrElse(default)
  }
}

class BehaviorExecutor(val behavior : Behavior) extends Serializable {
  import BehaviorExecutor.{badState, nullState}
  
//  val behavior = new Behavior(b.name, b.states.map(state => {
//    
//  }))
  
  var currentState : State = (nullState /: behavior.states)((a, b) => 
    if(a != nullState && b.initial) 
      throw new Exception("Multiple initial states defined for " + behavior.name + " behavior") 
    else if(b.initial) b 
    else a)
  if(currentState == nullState) throw new Exception("No initial state set for " + behavior.name + " behavior")
  
  def execute(area : Area, pop : Set[Person], p : Person) : Person = {
    val ctx = p.execContext
    ctx.bind("person", p)
    ctx.bind("area", area)
    
    try{
      synchronized{
        currentState.transitions.find(_(ctx)).foreach(t => 
          currentState = behavior.stateOf(t.destinationState, badState))
      }
    } catch {
      case e : Exception => {
        e.printStackTrace()
        currentState = badState
      }
    }
    return currentState.action.perform(ctx)
  }
}

object BehaviorExecutor{
  val nullState = new State("", false, null, null)
  val badState = new State("Broken State Machine", false, List(), Idle)
}