package edu.uccs.summers.data.population

import edu.uccs.summers.data.geometry.shapes.Vec2d
import org.jbox2d.common.Vec2

case class PhysicalProperties(val position : Vec2, val velocity : Vec2, val width : Double, val mass : Double = 1) {
  
}