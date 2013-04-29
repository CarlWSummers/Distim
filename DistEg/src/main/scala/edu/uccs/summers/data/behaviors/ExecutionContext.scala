package edu.uccs.summers.data.behaviors

import scala.collection.mutable.Map

case class ExecutionContext(parentContext : Option[ExecutionContext]) extends Serializable{

  private val bindings = Map[String, Any]()
  
  def bind(s : String, value : Any) = bindings += s -> value
  def unbind(s : String) = bindings -= s
  def dereference(s : String) : Option[Any] = {
    if(bindings.contains(s)) Some(bindings(s))
    else if(parentContext.isDefined) parentContext.get.dereference(s)
    else None
  }
  
  def dereference(s : String, default : Any) : Any = {
    var found = if(bindings.contains(s)){ Some(bindings(s)) }
//                else if(parentContext.get != null){ parentContext.get.dereference(s) }
                else { None }
    if(found.isDefined) found.get
    else {
      bindings.put(s, default)
      default
    }

  }
}