package edu.uccs.summers.data.geometry

import edu.uccs.summers.data.population.PopulationArchetypeDescriptor

case class InitialPopulationParameters(val minSize : Int, val maxSize : Int, val popTypes : List[PopulationArchetypeDescriptor])