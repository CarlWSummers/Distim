package edu.uccs.summers.data.geometry.shapes

import java.awt.Graphics2D
import scala.util.Random

abstract class Shape {

  def draw(g : Graphics2D) : Unit
  
  def generatePointWithin(rnd : Random) : Point
}