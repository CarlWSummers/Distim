package edu.uccs.summers.data

import edu.uccs.summers.data.behaviors.BehaviorExecutor
import edu.uccs.summers.data.behaviors.Behavior
import edu.uccs.summers.data.behaviors.BehaviorExecutor
import scala.util.Random
import edu.uccs.summers.data.geometry.shapes.Vec2d
import edu.uccs.summers.data.geometry.Area
import edu.uccs.summers.data.geometry.Collidable
import edu.uccs.summers.data.geometry.shapes.Circle
import edu.uccs.summers.data.population.PhysicalProperties

case class Person(val id : String, private var _executor : BehaviorExecutor, dynamics : PhysicalProperties, currentArea : Area) extends Collidable {

  def update(area : Area, pop : Set[Person]) = {
    _executor.execute(area, pop, this)
  }
  
  def executor = _executor
  def executor_= (value : BehaviorExecutor) = _executor = value
  
  def getShape() = {
    Circle(dynamics.position, 30)
  }
}