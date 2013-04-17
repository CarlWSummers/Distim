package edu.uccs.summers.ui

import scala.swing.Panel
import edu.uccs.summers.data.geometry.Area
import java.awt.Graphics2D
import scala.swing.event.MouseEntered
import scala.swing.event.MouseExited
import scala.swing.event.MouseMoved
import scala.swing.event.MouseDragged
import scala.swing.event.MouseWheelMoved
import java.awt.Color

class AreaCanvas extends Panel {

  private var area : Area = null
  private var translateX = 0.0;
  private var translateY = 0.0;
  private var scaleFactor = 1.0;
  private var drawMouseCoords = false;
  private var mouseX = 0;
  private var mouseY = 0;
  
  focusable = false
  opaque = true
  
  listenTo(mouse.clicks, mouse.moves, mouse.wheel)
  reactions += {
    case MouseEntered(_,_,_) => drawMouseCoords = true; 
    case MouseExited(_,_,_) => drawMouseCoords = false
    case MouseMoved(_,p,_) => {
      mouseX = p.getX.intValue
      mouseY = p.getY.intValue
    }
    case MouseDragged(_,p,_) => {
      translateX += (p.getX() - mouseX)
      translateY += (p.getY() - mouseY)
      mouseX = p.getX.intValue
      mouseY = p.getY.intValue
      repaint
    }
    case MouseWheelMoved(_, _, _, direction) => {
      if(direction < 0) scaleFactor /= 1.125
      else scaleFactor *= 1.125
      repaint
    }
  }

  def update(area : Area){
    this.area = area
    repaint
  }
  
  def reset(){
    translateX = 0
    translateY = 0
    scaleFactor = 1.0
    repaint
  }
  
  override def paintComponent(g : Graphics2D){
    if(area == null) {
      super.paintComponent(g);
      return
    }
    background = Color.BLACK
    super.paintComponent(g);
    
    g.translate(translateX, translateY)
    g.scale(scaleFactor, scaleFactor);
    area.draw(g);
  }
}