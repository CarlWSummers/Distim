package edu.uccs.summers.data

import edu.uccs.summers.data.behaviors.BehaviorExecutor
import edu.uccs.summers.data.behaviors.Behavior
import edu.uccs.summers.data.behaviors.BehaviorExecutor
import scala.util.Random
import edu.uccs.summers.data.geometry.shapes.Point
import edu.uccs.summers.data.geometry.Area

case class Person(val id : String, private var _executor : BehaviorExecutor, position : Point, velocity : Point, currentArea : Area) {

  def update(area : Area, pop : Set[Person]) = {
    _executor.execute(area, pop, this)
  }
  
  def executor = _executor
  def executor_= (value : BehaviorExecutor) = _executor = value
}