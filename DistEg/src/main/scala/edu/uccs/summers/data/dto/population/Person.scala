package edu.uccs.summers.data.dto.population

import edu.uccs.summers.data.dto.DTOType
import java.awt.Graphics2D
import org.jbox2d.common.Vec2
import java.awt.geom.Ellipse2D
import java.awt.Color
import java.awt.geom.Line2D
import scala.collection.mutable.ListBuffer
import java.awt.geom.Path2D
import java.awt.Paint
import java.awt.AlphaComposite

class Person(val position : Vec2, 
             val velocity : Vec2, 
             val angle : Float, 
             val visualRange : Float, 
             val visualFOV : Float, 
             val contacts : List[Vec2],
             val color : Color ) extends DTOType {

  def draw(g : Graphics2D, convertScalar : Float => Float, convertVec2 : Vec2 => Vec2) = {
    val pixelPos = convertVec2(position)
    val radius = convertScalar(.50f)
    val diameter = 2 * radius
    val shape = new Ellipse2D.Float(-radius, -radius, diameter, diameter)
    
    val oldTransform = g.getTransform
    val oldComposite = g.getComposite();

    g.translate(pixelPos.x, pixelPos.y)
    g.setColor(color)
    g.fill(shape)
    g.setColor(Color.GREEN)
    val pixelVelocity = convertVec2(velocity)
    g.draw(new Line2D.Float(0, 0, pixelVelocity.x, pixelVelocity.y))
    g.setColor(Color.RED)
    g.rotate(angle.toDouble)
    g.draw(new Line2D.Float(0, 0, 0, -radius))
    
    val visualRangePixel = convertScalar(visualRange)
    g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f))
    val poly = new Path2D.Float()
    poly.moveTo(0, 0)
    val segments = 16
    0 to segments-1 foreach (i => {
      val angle = math.toRadians((i / (segments-1).toFloat * visualFOV) + ((180 - visualFOV) / 2))
      poly.lineTo(visualRangePixel * math.cos(angle).toFloat, -(visualRangePixel * math.sin(angle).toFloat));
    })
    poly.closePath()
    g.fill(poly)
    
    g.setComposite(oldComposite)
    g.setTransform(oldTransform)

    g.setColor(color)
    contacts.map(convertVec2(_)).foreach(contact => {
      g.draw(new Line2D.Float(contact.x, contact.y, pixelPos.x, pixelPos.y))
      val trans = g.getTransform()
      g.translate(contact.x, contact.y)
      g.draw(new Line2D.Double(0, 0, math.cos(math.toRadians(160)) * 5, math.sin(math.toRadians(200)) * 5))
      g.setTransform(trans)
    })
    

  }
}