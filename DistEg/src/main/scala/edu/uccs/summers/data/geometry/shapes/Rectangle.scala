package edu.uccs.summers.data.geometry.shapes

import java.awt.Graphics2D

class Rectangle(ul : Point, width : Int, height : Int) extends Shape {

  def draw(g : Graphics2D) = {
    g.fillRect(ul.x, ul.y, width, height);
  }
}

object Rectangle {
  def apply(ul : Point, width : Int, height : Int) = new Rectangle(ul, width, height)
  def apply(ul : Point, lr : Point) = new Rectangle(ul, lr.x - ul.x, lr.y - ul.y)
}