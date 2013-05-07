package edu.uccs.summers.data.dto.population

import edu.uccs.summers.data.dto.DTOType
import edu.uccs.summers._
import java.awt.Graphics2D
import org.jbox2d.common.Vec2
import java.awt.geom.Ellipse2D
import java.awt.Color
import java.awt.geom.Line2D
import scala.collection.mutable.ListBuffer
import java.awt.geom.Path2D
import java.awt.Paint
import java.awt.AlphaComposite
import java.awt.BasicStroke
import edu.uccs.summers.data.population.PopulationArchetypeDescriptor
import edu.uccs.summers.data.population.PhysicalProperties

class Person(val name : String,
             val position : Vec2, // World Coordinates
             val velocity : Vec2, // In Meters / Sec
             val angle : Float, // In Radians
             val visualRange : Float, // Meters
             val visualFOV : Float, // Degrees on either side of 0
             val contacts : List[Vec2], // List of locations where someone is seen
             val color : Color,
             val archTypeName : String // Population Archetype Descriptor name
           ) extends DTOType {

  private val worldRadius = 0.5 // Half a meter radius
  
  def draw(g : Graphics2D, convertScalar : Float => Float, convertVec2 : Vec2 => Vec2, drawFlags : Int) = {
    
    val newContext = g.create().asInstanceOf[Graphics2D]
    
    val pixelPos = convertVec2(position)
    newContext.translate(pixelPos.x, pixelPos.y)
    
    if((drawFlags & Person.DRAW_VISUAL_CONTACTS) == Person.DRAW_VISUAL_CONTACTS)
      drawContactIndicators(newContext, contacts.map(position.sub).map(convertVec2))
    
    drawBody(newContext, convertVec2, convertScalar);
    drawRotationIndicator(newContext, convertVec2, convertScalar)
    
    if((drawFlags & Person.DRAW_VISUAL_RANGE) == Person.DRAW_VISUAL_RANGE)
      drawVisualRange(newContext, convertScalar)
  }

  def drawContactIndicators(g : Graphics2D, contacts : List[Vec2]){
    g.setColor(color)
    val stroke = g.getStroke()
    g.setStroke(new BasicStroke(.1f))
    contacts.foreach(contact => {
      g.draw(new Line2D.Float(0, 0, -contact.x, -contact.y))
    })
    g.setStroke(stroke)
  }
  
  def drawVisualRange(g : Graphics2D, convertScalar : Float => Float) {
    val composite = g.getComposite()
    
    val visualRangePixel = convertScalar(visualRange)
    val poly = new Path2D.Float()
    poly.moveTo(0, 0)
    val segments = 16
    0 to segments-1 foreach (i => {
      val angle = math.toRadians((i / (segments-1).toFloat * visualFOV) + ((180 - visualFOV) / 2))
      poly.lineTo(visualRangePixel * math.cos(angle).toFloat, -(visualRangePixel * math.sin(angle).toFloat));
    })
    poly.closePath()
    
    val transform = g.getTransform()
    g.rotate(convertAngle(angle))
    g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f))
    g.setColor(new Color(0, 10, 120))
    g.draw(poly)
    g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.2f))
    g.setColor(new Color(0, 80, 255))
    g.fill(poly)
    
    g.setTransform(transform)
    
    g.setComposite(composite)
  }
  
  def convertAngle(angle : Float) : Float = {
    -angle + math.toRadians(90).toFloat
  }
  
  def drawBody(g : Graphics2D, convertVec2 : Vec2 => Vec2, convertScalar : Float => Float) {
    val radius = convertScalar(.50f)
    val diameter = 2 * radius
    val shape = new Ellipse2D.Float(-radius, -radius, diameter, diameter)

    g.setColor(color)
    g.fill(shape)
  }
  
  def drawRotationIndicator(g : Graphics2D, convertVec2 : Vec2 => Vec2, convertScalar : Float => Float) {
    val stroke = g.getStroke()
    g.setStroke(new BasicStroke(.5f))

    val radius = convertScalar(.50f)
    val pixelVelocity = convertVec2(velocity)
    g.setColor(Color.GREEN)
    g.draw(new Line2D.Float(0, 0, pixelVelocity.x, pixelVelocity.y))
    g.setColor(Color.RED)
    
    val transform = g.getTransform()
    g.rotate(convertAngle(angle))
    g.draw(new Line2D.Float(0, 0, 0, -radius))
    g.setTransform(transform)
    
    g.setStroke(stroke)
  }
  
  def toPerson(desc : PopulationArchetypeDescriptor, position : Vec2) : data.Person = {
    val dynamics = new PhysicalProperties(position, new Vec2(0,0), 0, 1, 1)
    val newPerson = data.Person(name, null, dynamics, desc.name)
    newPerson.executor = desc.behavior.executor(newPerson)
    newPerson
  }
}

object Person{
  val DRAW_VISUAL_RANGE = 0x0001
  val DRAW_VISUAL_CONTACTS = 0x0010
}