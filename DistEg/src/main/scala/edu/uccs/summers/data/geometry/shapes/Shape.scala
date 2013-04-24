package edu.uccs.summers.data.geometry.shapes

import java.awt.Graphics2D
import scala.util.Random
import org.jbox2d.common.Vec2

abstract class Shape {
  def draw(g : Graphics2D, convertScaler : Float => Float, convertVec2 : Vec2 => Vec2) : Unit
  def generatePointWithin(rnd : Random) : Vec2
  def getOrigin() : Vec2
  def createCollidable() : org.jbox2d.collision.shapes.Shape
}