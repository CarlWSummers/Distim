package edu.uccs.summers.data.geometry.util

import org.jbox2d.collision.Manifold
import org.jbox2d.dynamics.contacts.Contact
import org.jbox2d.callbacks.ContactImpulse
import edu.uccs.summers.data.Person
import org.jbox2d.callbacks.ContactListener
import edu.uccs.summers.data.geometry.Exit
import scala.collection._
import edu.uccs.summers.data.geometry.AreaTransition
import edu.uccs.summers.data.geometry.TransitionEvent

class VisionListener extends ContactListener {
  
  private val _exitingPeople = mutable.Set[Person]()
  private val _transitioningPeople = mutable.Set[TransitionEvent]()
  
  def exitingPeople : immutable.Set[Person] = {
    _exitingPeople.toSet
  }
  
  def transitioningPeople : immutable.Set[TransitionEvent] = {
    _transitioningPeople.toSet
  }
  
  def clearExitingPeople() {
    _exitingPeople.clear
  }
  
  def clearTransitioningPeople() {
    _transitioningPeople.clear
  }
  
  def beginContact(contact : Contact){
    handleNewVisualContact(contact)
    handleExiting(contact)
    handleTransition(contact)
  }
  
  def handleTransition(contact : Contact) {
    val fixA = contact.getFixtureA()
    val fixB = contact.getFixtureB()

    (fixA.getUserData, fixB.getUserData) match {
      case (transition : AreaTransition, person : Person) => {
        _transitioningPeople += TransitionEvent(person, transition.destArea, transition.destName)
      }
      case (person : Person, transition : AreaTransition) => {
        _transitioningPeople += TransitionEvent(person, transition.destArea, transition.destName)
      }
      case _ => {}
    }
  }
  
  def handleNewVisualContact(contact : Contact) {
    val fixA = contact.getFixtureA()
    val fixB = contact.getFixtureB()
    
    val pair = (fixA.isSensor(), fixB.isSensor()) match {
      case (true, false) => {(fixA.getBody.getUserData, fixB.getBody.getUserData)}
      case (false, true) => {(fixB.getBody.getUserData, fixA.getBody.getUserData)}
      case _ => {return}
    }
    
    pair match {
      case (a : Person, b : Person) => {a.addVisualContact(b)}
      case _ => {}
    }
  }
  
  def handleExiting(contact : Contact) {
    val fixA = contact.getFixtureA()
    val fixB = contact.getFixtureB()
    
    val pair = (fixA.isSensor(), fixB.isSensor()) match {
      case (true, true) => {return}
      case (false, false) => {(fixA.getBody.getUserData, fixB.getBody.getUserData)}
      case (true, false) => {(fixA.getBody.getUserData, fixB.getBody.getUserData)}
      case (false, true) => {(fixB.getBody.getUserData, fixA.getBody.getUserData)}
    }
    
    pair match {
      case (a : Person, b : Person) => {a.addVisualContact(b)}
      case (p : Person, e : Exit) => { 
        println("Exiting!")
        _exitingPeople += p 
      }
      case (e : Exit, p : Person) => { 
        println("Exiting!")
        _exitingPeople += p 
      }
      case _ => {}
    }
  }

  def endContact(contact : Contact) {
    handleEndVisualContact(contact)
  }
  
  def handleEndVisualContact(contact:Contact){
    val fixA = contact.getFixtureA()
    val fixB = contact.getFixtureB()

    if(fixA.getBody.getUserData.isInstanceOf[Person] && fixA.isSensor() && fixB.getBody().getUserData().isInstanceOf[Person]){
      fixA.getBody.getUserData.asInstanceOf[Person].removeVisualContact(fixB.getBody.getUserData.asInstanceOf[Person])
    }else if (fixB.getBody.getUserData.isInstanceOf[Person] && fixB.isSensor() && fixA.getBody().getUserData().isInstanceOf[Person]){
      fixB.getBody.getUserData.asInstanceOf[Person].removeVisualContact(fixA.getBody.getUserData.asInstanceOf[Person])
    }
//    
//    val pair = (fixA.isSensor(), fixB.isSensor()) match {
//      case (true, true) => {return}
//      case (false, false) => {return}
//      case (true, false) => {(fixA.getBody.getUserData, fixB.getBody.getUserData)}
//      case (false, true) => {(fixB.getBody.getUserData, fixA.getBody.getUserData)}
//    }
//    pair match {
//      case (a : Person, b : Person) => {
//        a.removeVisualContact(b)
//        b.removeVisualContact(a)
//      }
//      case _ => {}
//    }
  }

  def preSolve(contact : Contact, oldManifold : Manifold) = {
    
  }

  def postSolve(contact : Contact, impulse : ContactImpulse) = {
    
  }
}