package edu.uccs.summers

import org.jbox2d.common.Vec2
import org.jbox2d.testbed.framework.TestbedTest

object Test {
  def getTestName(): String = "Carl Test"
  
  def initTest(x$1: Boolean): Unit = {}
  
  val lists = List(List(None, Some(-1), Some(0), Some(1)), List(Some(-1), Some(0), None, Some(1)))
  
  def maxOfIntOptions( a : (Option[Int], Option[Int])) : Option[Int] = {
    a match {
      case (None, None) => None
      case (a : Some[Int], None) => a
      case (None, b : Some[Int]) => b
      case (a : Some[Int], b : Some[Int]) => if(a.get > b.get) a else b
    }
  }
  
  val maxList = lists reduce (_ zip _ map maxOfIntOptions)
  println (maxList)
  
  def main(args : Array[String]){
    println("Hello World")
  }
//  def main(args : Array[String]){
//    val vecs = new Vec2(0, 1) :: new Vec2(.5f,.5f) :: new Vec2(1, 0) :: 
//               new Vec2(.5f, -.5f) :: new Vec2(0, -1) :: new Vec2(-.5f, -.5f) :: 
//               new Vec2(-1, 0) :: new Vec2(-.5f, .5f) :: new Vec2(0, 1) :: Nil
//    vecs.foreach(vec => {
//      val normalizedAngle = (360 + math.toDegrees(math.atan2(vec.y, vec.x)) - 90) % 360 //if(angle > 0) angle else (360 + angle) % 360
//      println(vec + " : " + normalizedAngle)
//    })
//  }
}