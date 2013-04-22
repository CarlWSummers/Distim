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
import edu.uccs.summers.data.geometry.shapes.Vec2d

class AreaCanvas extends Panel {

  private var area : Area = null
  private var pop : Set[Person] = null
  
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

  def update(area : Area, pop : Set[Person]){
    this.area = area
    this.pop = pop
    repaint
  }
  
  def reset(){
    translateX = 0
    translateY = 0
    scaleFactor = 1.0
    repaint
  }
  
  override def paintComponent(g : Graphics2D){
    background = Color.LIGHT_GRAY
    super.paintComponent(g)
    if(area == null) return
    
    g.translate(size.width / 2, size.height / 2)
    g.scale(scaleFactor, scaleFactor)
    g.translate(translateX, translateY)
    area.draw(g);
    
    g.setColor(Color.MAGENTA)
    pop.foreach(person => {
      val dyn = person.dynamics
      val halfWidth = Math.round(dyn.width / 2).toInt
      
      g.fillOval(Math.round(dyn.position.x).toInt - halfWidth, Math.round(dyn.position.y).toInt - halfWidth, dyn.width.toInt, dyn.width.toInt)
      g.setColor(Color.RED)
      val next = person.dynamics.position + person.dynamics.velocity
      g.drawLine(Math.round(person.dynamics.position.x).toInt, Math.round(person.dynamics.position.y).toInt, Math.round(next.x).toInt, Math.round(next.y).toInt)
      g.setColor(Color.RED.darker)
      val nextTwo = person.dynamics.position + (person.dynamics.velocity * 2)
      g.drawLine(Math.round(next.x).toInt, Math.round(next.y).toInt, Math.round(nextTwo.x).toInt,  Math.round(nextTwo.y).toInt)
      val obj = person.dynamics
      
      val aabb = Rectangle(obj.position - Vec2d(halfWidth, halfWidth), dyn.width.toInt, dyn.width.toInt)
      g.drawRect(aabb.ul.x.toInt, aabb.ul.y.toInt, aabb.width.toInt, aabb.height.toInt)
      g.setColor(Color.BLACK)
      g.drawOval(obj.position.x.toInt, obj.position.y.toInt, 1, 1)
    })
  }
}