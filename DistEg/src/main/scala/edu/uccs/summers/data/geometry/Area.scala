package edu.uccs.summers.data.geometry

import java.awt.Graphics2D
import java.awt.Color

class Area(name : String, objects : List[StaticEntity]) {

  val bgColor = new Color(25, 99, 40)
  
  def draw(g : Graphics2D){
    objects.foreach(entity => entity.draw(g))
  }
}

object Area{
  def apply(name : String, objects : List[StaticEntity]) = {
    new Area(name, objects)
  }
}