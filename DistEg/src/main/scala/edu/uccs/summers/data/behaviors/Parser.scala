package edu.uccs.summers.data.behaviors

import scala.collection.mutable.HashMap
import scala.util.parsing.combinator.JavaTokenParsers
import scala.util.parsing.combinator.PackratParsers

import edu.uccs.summers.data.behaviors.interpreter.And
import edu.uccs.summers.data.behaviors.interpreter.ArgumentListExpression
import edu.uccs.summers.data.behaviors.interpreter.Equal
import edu.uccs.summers.data.behaviors.interpreter.Expression
import edu.uccs.summers.data.behaviors.interpreter.False
import edu.uccs.summers.data.behaviors.interpreter.GreaterThan
import edu.uccs.summers.data.behaviors.interpreter.LessThan
import edu.uccs.summers.data.behaviors.interpreter.MemberAccessExpression
import edu.uccs.summers.data.behaviors.interpreter.MemberInvocationExpression
import edu.uccs.summers.data.behaviors.interpreter.Not
import edu.uccs.summers.data.behaviors.interpreter.NumericExpression
import edu.uccs.summers.data.behaviors.interpreter.Or
import edu.uccs.summers.data.behaviors.interpreter.StringExpression
import edu.uccs.summers.data.behaviors.interpreter.True
import edu.uccs.summers.data.behaviors.interpreter.VariableExpression

class BehaviorsParser(context : ParsingContext) extends JavaTokenParsers with PackratParsers{
  val bindings = new HashMap[String, Any]
  val stateMap = new HashMap[String, State]
  
  def behaviorListing = rep(behavior)
  
  def behavior = "def" ~ "behavior" ~ behaviorName ~ "{" ~ behaviorBody ~ "}" ^^ { 
    case _ ~ _ ~ name ~ _ ~ body ~ _ =>
      new Behavior(name, body)
  }
  def behaviorName = ident
  
  def behaviorBody = bodyElement ^^ { bodyElement =>
    bodyElement
  }
  def bodyElement = rep(state)
  
  def state = "def" ~ opt("initial") ~ "state" ~ stateName ~ "{" ~ stateBody ~ "}" ^^ {
    case _ ~ initial ~ _ ~ name ~ _ ~ body ~ _ =>
      val state = new State(name, initial.isDefined, body._1, body._2)
      stateMap += (name -> state)
      state
  }
  def stateName = ident
  def stateBody = "transitions" ~ ":" ~ "{" ~ rep(transition) ~ "}" ~ action ^^ {
    case _ ~ _ ~ _ ~ transitions ~ _ ~ action =>
      (transitions, action)
  }
  
  def action = "action" ~> opt(":" ~> ident) ~ "{" ~ opt(actionBody) ~ "}" ^^ {
    case parent ~ _ ~ body ~ _ =>
      var parentAction : Action = retrieveBinding(parent get, new Action(None, "ERROR"))
      if(parentAction.body == "ERROR") println ("Warning: '" + parent.get + "' is not a known super action")
      new Action(Some(parentAction), body.getOrElse(""))
  }
  def actionBody = "take Any from OpenMoves"
  
  def transition = predicate ~ "->" ~ ident ^^ {
    case pred ~ _ ~ destinationState =>
     Transition(destinationState, pred)
  }
  
  def predicate = (
      logicalOrExpression
  )
  
  lazy val primaryExpression : PackratParser[Expression] = (
      constant
    | stringLiteral ^^ {
        case string => StringExpression(string)
      }
      
    | ident ^^ { case string => VariableExpression(string)}
    
    | "(" ~ logicalOrExpression ~ ")" ^^ {
        case _ ~ expr ~ _ => expr
      }
  )
  
  lazy val postfixExpression : PackratParser[Expression] = (
      postfixExpression ~ "." ~ ident ~ "(" ~ argumentExpressionList ~ ")" ^^ {
        case expr ~ _ ~ ident ~ _ ~ args ~ _ => MemberInvocationExpression(expr, ident, Some(args))
      }
      
    | postfixExpression ~ "." ~ ident ~ "(" ~ ")" ^^ {
        case expr ~ _ ~ ident ~ _ ~ _ => MemberInvocationExpression(expr, ident, None)
      }
        
    | postfixExpression ~ "." ~ ident ^^ {
        case expr ~ _ ~ ident => MemberAccessExpression(expr, ident)
      }
    
    | primaryExpression
  )
  
  def argumentExpressionList : PackratParser[ArgumentListExpression] = (
      logicalOrExpression ~ "," ~ argumentExpressionList ^^ {
        case expr ~ _ ~ args => ArgumentListExpression(Some(args), expr)
      }
      
    | logicalOrExpression ^^ {
        case expr => ArgumentListExpression(None, expr)
      }
  )
    
  lazy val unaryExpression : PackratParser[Expression] = (
    unaryOperator ~ unaryExpression ^^ {
        case op ~ expr => new Not(expr)
      }
    | postfixExpression
  )
  
  def unaryOperator = (
      "!"
  )
  
  lazy val relationalExpression : PackratParser[Expression] = (
      relationalExpression ~ "<" ~ unaryExpression ^^ {
        case lhs ~ _ ~ rhs => LessThan(lhs, rhs)
      }
    | relationalExpression ~ ">" ~ unaryExpression ^^ {
        case lhs ~ _ ~ rhs => GreaterThan(lhs, rhs)
      }
    | relationalExpression ~ "<=" ~ unaryExpression ^^ {
        case lhs ~ _ ~ rhs => Or(LessThan(lhs, rhs), Equal(lhs, rhs))
      }
    | relationalExpression ~ ">=" ~ unaryExpression ^^ {
        case lhs ~ _ ~ rhs => Or(GreaterThan(lhs, rhs), Equal(lhs, rhs))
      }
    | unaryExpression
  )
  
  lazy val equalityExpression : PackratParser[Expression] = (
      equalityExpression ~ "==" ~ relationalExpression ^^ {
        case lhs ~ _ ~ rhs => Equal(lhs, rhs)
      }
    | equalityExpression ~ "!=" ~ relationalExpression ^^ {
        case lhs ~ _ ~ rhs => Not(Equal(lhs, rhs))
      }
    | relationalExpression
  )
  
  lazy val logicalAndExpression : PackratParser[Expression] = (
      logicalAndExpression ~ "&&" ~ equalityExpression ^^ {
        case lhs ~ _ ~ rhs => And(lhs, rhs)
      }
    | equalityExpression
  )
  
  lazy val logicalOrExpression : PackratParser[Expression] = (
      logicalOrExpression ~ "||" ~ logicalAndExpression ^^ {
        case lhs ~ _ ~ rhs => Or(lhs, rhs)
      }
    | logicalAndExpression
  )
  
  def constant = (
      floatingPointNumber ^^ { case num => new NumericExpression(num.toDouble) }
    | decimalNumber ^^ { case num => new NumericExpression(num.toDouble) }
    | wholeNumber ^^ { case num => new NumericExpression(num.toDouble) }
    | "true" ^^ { _ => True }
    | "false" ^^ { _ => False }
  )
  
  def bind(key : String, obj : Any) = bindings += key -> obj
  def retreiveBinding[T](key : String) = bindings.get(key).asInstanceOf[T]
  def retrieveBinding[T](key : String, default : T) : T = bindings.getOrElse(key, default).asInstanceOf[T]
}
