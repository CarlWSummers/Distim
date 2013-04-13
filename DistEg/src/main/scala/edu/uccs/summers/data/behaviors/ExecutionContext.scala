package edu.uccs.summers.data.behaviors

import scala.collection.mutable.Map

case class ExecutionContext(parentContext : Option[ExecutionContext]){

  private val bindings = Map[String, Any]()
  
  def bind(s : String, value : Any) = bindings += s -> value
  def unbind(s : String) = bindings -= s
  def dereference(s : String) : Any = {
    if(bindings.contains(s)) bindings(s)
    else if(parentContext.isDefined) parentContext.get.dereference(s)
    else null
  }
}