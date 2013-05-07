package edu.uccs.summers.ui

import java.awt.Color
import java.awt.Graphics2D
import scala.swing.Panel
import scala.swing.event.MouseDragged
import scala.swing.event.MouseEntered
import scala.swing.event.MouseExited
import scala.swing.event.MouseMoved
import scala.swing.event.MouseWheelMoved
import org.jbox2d.common.Vec2
import edu.uccs.summers.data.dto.geometry.Area
import java.awt.Font
import java.awt.RenderingHints
import scala.swing.event.MouseClicked

class AreaCanvas extends Panel {

  private var area : Area = null
  private var elapsedRealTime : Long = 0
  
  private var translateX = 0.0
  private var translateY = 0.0
  private var scaleFactor = 1.5
  private var drawMouseCoords = false
  private var mouseX = 0
  private var mouseY = 0
  private var showVisualContacts = false
  private var showVisualRange = false
  
  focusable = false
  opaque = true
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
    case ev : MouseClicked => {
      (showVisualContacts, showVisualRange) match {
        case (true, true) => showVisualContacts = false; showVisualRange = false
        case (false, false) => showVisualContacts = true; showVisualRange = false
        case (true, false) => showVisualContacts = false; showVisualRange = true
        case (false, true) => showVisualContacts = true; showVisualRange = true
      }
      area.drawVisualContacts = showVisualContacts
      area.drawVisualRange = showVisualRange
      repaint
    }
  }

  def update(area : Area, elapsedTime : Long){
    this.area = area
    area.drawVisualContacts = showVisualContacts
    area.drawVisualRange = showVisualRange
    this.elapsedRealTime = elapsedTime
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
    
    g.setRenderingHint(
      RenderingHints.KEY_ANTIALIASING,
      RenderingHints.VALUE_ANTIALIAS_ON)
    g.setFont(new Font("Monospaced", Font.PLAIN, 12))
    g.setColor(Color.WHITE)
    g.drawString("Simulation Time : " + "%.2f seconds".format(area.elapsedTime), 3, size.height - 3)
    g.drawString("   Compute Time : " + "%d milliseconds".format(elapsedRealTime), 3, size.height - 23)
    g.drawString("Area Population : " + area.pop.size, 3, size.height - 43)
    
    
    val m = g.getTransform()
    
    g.translate(size.width / 2, size.height / 2)
    g.scale(scaleFactor, scaleFactor)
    g.translate(translateX, translateY)
    drawAxes(g)
    g.setColor(Color.WHITE)
    area.draw(g, (f) => (scaleFactor * f).toFloat, (vec) => new Vec2((vec.x * scaleFactor).toFloat, (-vec.y * scaleFactor).toFloat))
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