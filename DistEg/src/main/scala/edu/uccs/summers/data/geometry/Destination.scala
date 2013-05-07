package edu.uccs.summers.data.geometry

import edu.uccs.summers.data.dto.HasDTO
import edu.uccs.summers.data.geometry.shapes.Shape
import edu.uccs.summers.data.dto.geometry.{Destination => DestinationDTO}

class Destination(val name : String, val shape : Shape) extends StaticEntity(shape) {

  override def translate() = {
    new DestinationDTO(name, shape)
  }
}