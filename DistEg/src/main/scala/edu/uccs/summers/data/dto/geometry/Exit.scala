package edu.uccs.summers.data.dto.geometry

import java.awt.Graphics2D

import org.jbox2d.common.Vec2

import edu.uccs.summers.data.dto.DTOType

class Exit extends StaticEntity with DTOType {

  def draw(g : Graphics2D, convertScalar : Float => Float, convertVec2 : Vec2 => Vec2) = {
    throw new UnsupportedOperationException
  }
}