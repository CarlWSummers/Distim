package edu.uccs.summers.data

import edu.uccs.summers.data.behaviors.BehaviorExecutor
import edu.uccs.summers.data.behaviors.Behavior
import edu.uccs.summers.data.behaviors.BehaviorExecutor
import scala.util.Random
import edu.uccs.summers.data.geometry.shapes.Point

case class Person(val id : String, private var _executor : BehaviorExecutor, position : Point) {

  def update(t : Topography, pop : Population) = {
    _executor.execute(t, pop, this)
  }
  
  def executor = _executor
  def executor_= (value : BehaviorExecutor) = _executor = value
}

case class TestClass(value : Int) {} 