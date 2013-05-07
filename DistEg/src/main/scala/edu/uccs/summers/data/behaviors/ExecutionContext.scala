package edu.uccs.summers.data.behaviors

import scala.collection.mutable.Map

case class ExecutionContext(parentContext : ExecutionContext) extends Serializable{

  private val bindings = Map[String, Any]()
  
  def bind(s : String, value : Any) = bindings += s -> value
  def unbind(s : String) = bindings -= s
  
  def dereference(s : String) : Option[Any] = {
    if(bindings.contains(s)) return Some(bindings(s))
    if(parentContext != null) return parentContext.dereference(s) else return None
  }
  
  def dereference[T](s : String, default : T) : T = {
    if(bindings.contains(s)){
      return bindings(s).asInstanceOf[T]
    }else{
      val parentBinding = if(parentContext != null) parentContext.dereference(s) else None
      if(parentBinding.isDefined) 
        return parentBinding.get.asInstanceOf[T]
      else{
        bindings += s -> default
        return default
      }
    }
  }
}