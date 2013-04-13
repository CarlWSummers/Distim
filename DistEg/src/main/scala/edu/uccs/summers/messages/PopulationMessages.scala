package edu.uccs.summers.messages

import edu.uccs.summers.data.Population

sealed trait PopulationMessage

case object PopulationRequest extends PopulationMessage
case class PopulationResponse(pop : Population) extends PopulationMessage