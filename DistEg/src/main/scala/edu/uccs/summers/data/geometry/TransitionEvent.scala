package edu.uccs.summers.data.geometry

import edu.uccs.summers.data.Person
import edu.uccs.summers.data.dto.HasDTO
import edu.uccs.summers.data.dto.geometry.{TransitionEvent => TransitionEventDTO}

case class TransitionEvent(person : Person, areaName : String, destName : String) extends HasDTO[TransitionEventDTO]{

  override def translate() : TransitionEventDTO = {
    TransitionEventDTO(person.translate, areaName : String, destName : String)
  }
}