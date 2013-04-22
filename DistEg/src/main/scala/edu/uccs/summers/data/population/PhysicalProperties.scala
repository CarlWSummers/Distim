package edu.uccs.summers.data.population

import edu.uccs.summers.data.geometry.shapes.Vec2d

case class PhysicalProperties(val position : Vec2d, val velocity : Vec2d, val width : Double, val mass : Double = 1) {
  
}