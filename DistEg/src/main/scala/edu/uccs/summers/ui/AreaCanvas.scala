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
import edu.uccs.summers.data.Person
import edu.uccs.summers.data.geometry.shapes.Rectangle
import org.jbox2d.common.Vec2

class AreaCanvas extends Panel {

  private var area : Area = null
  
  private var translateX = 0.0
  private var translateY = 0.0
  private var scaleFactor = 1.5
  private var drawMouseCoords = false
  private var mouseX = 0
  private var mouseY = 0
  
  focusable = false
  opaque = true
  println("NEW");
  listenTo(mouse.clicks, mouse.moves, mouse.wheel)
  reactions += {
    case MouseEntered(_,_,_) => drawMouseCoords = true 
    case MouseExited(_,_,_) => drawMouseCoords = false
    case MouseMoved(_,p,_) => {
      mouseX = p.getX.intValue
      mouseY = p.getY.intValue
    }
    case MouseDragged(_,p,_) => {
      translateX += (p.getX() - mouseX) / scaleFactor
      translateY += (p.getY() - mouseY) / scaleFactor
      mouseX = p.getX.intValue
      mouseY = p.getY.intValue
      repaint
    }
    case ev : MouseWheelMoved => {
      val modifier = if(ev.peer.isControlDown()) 1.03125 else if(ev.peer.isShiftDown()) 1.25 else 1.125
      if(ev.rotation > 0) scaleFactor /= modifier
      else scaleFactor *= modifier
      repaint
    }
  }

  def update(area : Area){
    this.area = area
    println("scheduled paint");
    repaint
  }
  
  def reset(){
    translateX = 0
    translateY = 0
    scaleFactor = 1.5
    repaint
  }
  
  override def paintComponent(g : Graphics2D){
    background = Color.BLACK
    super.paintComponent(g)
    if(area == null) return
    val m = g.getTransform();
    
    g.translate(size.width / 2, size.height / 2)
    g.scale(scaleFactor, scaleFactor)
    g.translate(translateX, translateY)
    drawAxes(g)
    g.setColor(Color.WHITE)
    area.draw(g, (f) => (scaleFactor * f).toFloat, (vec) => new Vec2((vec.x * scaleFactor).toFloat, (-vec.y * scaleFactor).toFloat));
    g.setTransform(m)
  }
  
  def drawAxes(g : Graphics2D){
    val scaledThousand = (1000 * scaleFactor).toInt
    g.drawLine(0, -scaledThousand, 0, scaledThousand)
    g.drawLine(-scaledThousand, 0, scaledThousand, 0)
    val width = Math.max(1, (5 * scaleFactor).toInt)
    -1000 to 1000 by 10 map (i => (i * scaleFactor).toInt) foreach (i => {g.drawLine(-width, i, width, i)})
    -1000 to 1000 by 10 map (i => (i * scaleFactor).toInt) foreach (i => {g.drawLine(i, -width, i, width)})
  }
}