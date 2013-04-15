package edu.uccs.summers.data.geometry

import edu.uccs.summers.data.population.PopulationArchetypeDescriptor

case class InitialPopulationParameters(minSize : Int, maxSize : Int, popTypes : List[PopulationArchetypeDescriptor])