package edu.uccs.summers.data

import edu.uccs.summers.data.behaviors.BehaviorExecutor
import edu.uccs.summers.data.behaviors.Behavior
import edu.uccs.summers.data.behaviors.BehaviorExecutor
import scala.util.Random

case class Person(val id : String, name : String, male : Boolean, private var _executor : BehaviorExecutor, position : Position) {

  val test = TestClass(name.length())
  def testMethod(lower : Double, upper : Double) = Random.nextInt(upper.toInt - lower.toInt) + lower.toInt
  def getName() : String = name
  
  def update(t : Topography, pop : Population) = {
    _executor.execute(t, pop, this)
  }
  
  def executor = _executor
  def executor_= (value : BehaviorExecutor) = _executor = value
}

case class TestClass(value : Int) {} 