package edu.uccs.summers.data.dto

trait HasDTO[T <: DTOType] {
  def translate() : T
}