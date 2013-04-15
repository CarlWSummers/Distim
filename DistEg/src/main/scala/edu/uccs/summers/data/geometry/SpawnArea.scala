package edu.uccs.summers.data.geometry

import edu.uccs.summers.data.geometry.shapes.Shape

case class SpawnArea(shape : Shape) extends StaticEntity(shape){
  def isCollidable = false
}