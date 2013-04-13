package edu.uccs.summers.data.behaviors

import scala.util.Random

import edu.uccs.summers.data.Open
import edu.uccs.summers.data.Person
import edu.uccs.summers.data.Population
import edu.uccs.summers.data.Position
import edu.uccs.summers.data.Topography


class Behavior (val name : String, val states : List[State]){
  val nullState = new State("", false, null, null)
  def executor (p : Person) = new BehaviorExecutor(this)
  def state(s : String, default : State) : State = {
    states.find(_.name == s).getOrElse(default)
  }
}

class BehaviorExecutor(val behavior : Behavior) {
  import BehaviorExecutor.{badState, nullState}
  
  var currentState : State = (nullState /: behavior.states)((a, b) => 
    if(a != nullState && b.initial) 
      throw new Exception("Multiple initial states defined for " + behavior.name + " behavior") 
    else if(b.initial) b 
    else a)
  if(currentState == nullState) throw new Exception("No initial state set for " + behavior.name + " behavior")
  
  def execute(t : Topography, pop : Population, p : Person) : Person = {
    val ctx = ExecutionContext(None)
    ctx.bind("person", p)
    ctx.bind("terrain", t)
    ctx.bind("population", pop)
    ctx.bind("Population", pop)
    ctx.bind("Random", Random)
    
    try{
      currentState.transitions.find(_(ctx).asInstanceOf[Boolean]).map(t => 
        currentState = behavior.state(t.destinationState, badState))
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
  val badState = new State("Broken State Machine", false, List(), new Idle)
}