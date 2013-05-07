package edu.uccs.summers.data.dto.geometry

import edu.uccs.summers.data.dto.DTOType
import edu.uccs.summers.data.dto.population.Person

case class TransitionEvent(person : Person, areaName : String, destName : String) extends DTOType{

}