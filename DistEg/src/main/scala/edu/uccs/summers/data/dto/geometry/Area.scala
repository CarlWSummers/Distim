package edu.uccs.summers.data.dto.geometry

import edu.uccs.summers.data.dto.DTOType
import java.awt.Graphics2D
import org.jbox2d.common.Vec2
import edu.uccs.summers.data.dto.population.Person

class Area(val name : String, val areaBounds : AreaBounds, val objects : List[StaticEntity], val pop : Set[Person], val elapsedTime : Float) extends DTOType {

  val bgColor = areaBounds.getColor
  
  def draw(g : Graphics2D, convertScalar : Float => Float, convertVec2 : Vec2 => Vec2){
    g.setColor(bgColor)
    areaBounds.draw(g, convertScalar, convertVec2)
    objects.foreach(entity => {
      val matrix = g.getTransform
      entity.draw(g, convertScalar, convertVec2)
      g.setTransform(matrix)
    })
    pop.foreach(person => {
      val matrix = g.getTransform
      person.draw(g, convertScalar, convertVec2)
      g.setTransform(matrix)
    })
  }

}