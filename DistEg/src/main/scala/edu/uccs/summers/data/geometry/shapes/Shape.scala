package edu.uccs.summers.data.geometry.shapes

import java.awt.Graphics2D

abstract class Shape {

  def draw(g : Graphics2D) : Unit
}