package edu.uccs.summers

import org.jbox2d.common.Vec2

object Test {
  def main(args : Array[String]){
    val vecs = new Vec2(0, 1) :: new Vec2(.5f,.5f) :: new Vec2(1, 0) :: 
               new Vec2(.5f, -.5f) :: new Vec2(0, -1) :: new Vec2(-.5f, -.5f) :: 
               new Vec2(-1, 0) :: new Vec2(-.5f, .5f) :: new Vec2(0, 1) :: Nil
    vecs.foreach(vec => {
      val normalizedAngle = (360 + math.toDegrees(math.atan2(vec.y, vec.x)) - 90) % 360 //if(angle > 0) angle else (360 + angle) % 360
      println(vec + " : " + normalizedAngle)
    })
  }
}