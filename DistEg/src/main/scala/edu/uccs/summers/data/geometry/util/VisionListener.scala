package edu.uccs.summers.data.geometry.util

import org.jbox2d.collision.Manifold
import org.jbox2d.dynamics.contacts.Contact
import org.jbox2d.callbacks.ContactImpulse
import edu.uccs.summers.data.Person
import org.jbox2d.callbacks.ContactListener
import edu.uccs.summers.data.geometry.Exit
import scala.collection._

class VisionListener extends ContactListener {
  
  private val _exitingPeople = mutable.Set[Person]()
  
  def exitingPeople : immutable.Set[Person] = {
    _exitingPeople.toSet
  }
  
  def clearExitingPeople() {
    _exitingPeople.clear
  }
  
  def beginContact(contact : Contact) = {
    (contact.getFixtureA().getBody().getUserData(), contact.getFixtureB().getUserData()) match {
      case (a : Person, b : Person) => {
        a.addVisualContact(b)
      }
      case _ => {}
    }
    (contact.getFixtureA().getUserData(), contact.getFixtureB().getBody().getUserData()) match {
      case (a : Person, b : Person) => {
        a.addVisualContact(b)
      }
      case _ => {}
    }
    
    (contact.getFixtureA().getUserData(), contact.getFixtureB().getUserData()) match {
      case (a : Person, b : Exit) => {
        _exitingPeople += a
      }
      case (a : Exit, b : Person) => {
        _exitingPeople += b
      }
      case _ => {}
    }
  }

  def endContact(contact : Contact) = {
    (contact.getFixtureA().getBody().getUserData(), contact.getFixtureB().getUserData()) match {
      case (a : Person, b : Person) => {
        a.removeVisualContact(b)
      }
      case _ => {}
    }
    (contact.getFixtureA().getUserData(), contact.getFixtureB().getBody().getUserData()) match {
      case (a : Person, b : Person) => {
        a.removeVisualContact(b)
      }
      case _ => {}
    }
  }

  def preSolve(contact : Contact, oldManifold : Manifold) = {
    
  }

  def postSolve(contact : Contact, impulse : ContactImpulse) = {
    
  }
}