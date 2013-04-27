package edu.uccs.summers.data.behaviors

case class State (val name : String, val initial : Boolean, val transitions : List[Transition], val action : Action) extends Serializable {
  
}