package edu.uccs.summers.data.behaviors.interpreter

import edu.uccs.summers.data.behaviors.ExecutionContext
import edu.uccs.summers.data.behaviors.ExecutionContext
import scala.math.Numeric
import scala.math.Ordering
import edu.uccs.summers.data.behaviors.ExecutionContext
import edu.uccs.summers.data.behaviors.ExecutionContext

trait Expression {
  def apply(ctx : ExecutionContext) : Any 
}

trait BooleanExpression extends Expression {
  override def apply(ctx : ExecutionContext) : Boolean
}

case class LessThan(lhs : Expression, rhs : Expression) extends BooleanExpression {
  override def apply(ctx : ExecutionContext) = {
    (lhs, rhs) match {
      case (lhs : NumericExpression, rhs : NumericExpression) => {
        lhs(ctx) < rhs(ctx)
      }
      case (lhs : ValueExpression, rhs : NumericExpression) => {
        LessThan(lhs(ctx), rhs)(ctx)
      }
      case (lhs : NumericExpression, rhs : ValueExpression) => {
        LessThan(lhs, rhs(ctx))(ctx)
      }
      case (lhs : MemberAccessExpression, rhs : NumericExpression) => {
        LessThan(lhs(ctx), rhs)(ctx)
      }
      case (lhs : NumericExpression, rhs : MemberAccessExpression) => {
        LessThan(lhs, rhs(ctx))(ctx)
      }
      case (lhs : MemberInvocationExpression, rhs : NumericExpression) => {
        LessThan(lhs(ctx), rhs)(ctx)
      }
      case (lhs : NumericExpression, rhs : MemberInvocationExpression) => {
        LessThan(lhs, rhs(ctx))(ctx)
      }
      case (lhs : Variable, rhs : NumericExpression) => {
        LessThan(ValueExpression(lhs(ctx)), rhs)(ctx)
      }
      case (lhs : NumericExpression, rhs : Variable) => {
        LessThan(lhs, ValueExpression(rhs(ctx)))(ctx)
      }
      case _ => throw new Exception("Incompatible Types " + lhs.getClass() + " < " + rhs.getClass())
    }
  }
}

case class GreaterThan(lhs : Expression, rhs : Expression) extends BooleanExpression {
  override def apply(ctx : ExecutionContext) = {
    (lhs, rhs) match {
      case (lhs : NumericExpression, rhs : NumericExpression) => {
        lhs(ctx) > rhs(ctx)
      }
      case _ => throw new Exception("Incompatible Types " + lhs.getClass() + " < " + rhs.getClass())
    }
  }
}

case class Equal(lhs : Expression, rhs : Expression) extends BooleanExpression {
  override def apply(ctx : ExecutionContext) = {
    (lhs, rhs) match {
      case (lhs : NumericExpression, rhs : NumericExpression) => {
        lhs(ctx) == rhs(ctx)
      }
      case (lhs : StringExpression, rhs : StringExpression) => {
        lhs(ctx) == rhs(ctx)
      }
      case (lhs : ValueExpression, rhs : StringExpression) => {
        Equal(lhs(ctx), rhs)(ctx)
      }
      case (lhs : StringExpression, rhs : ValueExpression) => {
        Equal(lhs, rhs(ctx))(ctx)
      }
      case (lhs : MemberAccessExpression, rhs : StringExpression) => {
        Equal(lhs(ctx), rhs)(ctx)
      }
      case (lhs : StringExpression, rhs : MemberAccessExpression) => {
        Equal(lhs, rhs(ctx))(ctx)
      }
      case (lhs : Variable, rhs : StringExpression) => {
        Equal(ValueExpression(lhs(ctx)), rhs)(ctx)
      }
      case (lhs : StringExpression, rhs : Variable) => {
        Equal(lhs, ValueExpression(rhs(ctx)))(ctx)
      }
      case (lhs : BooleanExpression, rhs : BooleanExpression) => {
        lhs(ctx) == rhs(ctx)
      }
      case (lhs : ValueExpression, rhs : BooleanExpression) => {
        Equal(lhs(ctx), rhs)(ctx)
      }
      case (lhs : BooleanExpression, rhs : ValueExpression) => {
        Equal(lhs, rhs(ctx))(ctx)
      }
      case (lhs : MemberAccessExpression, rhs : BooleanExpression) => {
        Equal(lhs(ctx), rhs)(ctx)
      }
      case (lhs : BooleanExpression, rhs : MemberAccessExpression) => {
        Equal(lhs, rhs(ctx))(ctx)
      }
      case (lhs : Variable, rhs : BooleanExpression) => {
        Equal(ValueExpression(lhs(ctx)), rhs)(ctx)
      }
      case (lhs : BooleanExpression, rhs : Variable) => {
        Equal(lhs, ValueExpression(rhs(ctx)))(ctx)
      }
      case _ => throw new Exception("Incompatible Types " + lhs.getClass() + " < " + rhs.getClass())
    }
  }
}

case class Or(lhs : Expression, rhs : Expression) extends BooleanExpression {
  override def apply(ctx : ExecutionContext) = {
    (lhs, rhs) match {
      case (lhs : BooleanExpression, rhs : BooleanExpression) => {
        lhs(ctx) || rhs(ctx)
      }
      case _ => throw new Exception("Incompatible Types")
    }
  }
}

case class And(lhs : Expression, rhs : Expression) extends BooleanExpression {
  override def apply(ctx : ExecutionContext) = {
    (lhs, rhs) match {
    case (lhs : BooleanExpression, rhs : BooleanExpression) => {
      lhs(ctx) && rhs(ctx)
    }
    case _ => throw new Exception("Incompatible Types")
    }
  }
}

case class Not(unary : Expression) extends BooleanExpression {
  override def apply(ctx : ExecutionContext) = {
    (unary) match {
      case unary : BooleanExpression => {
        !unary(ctx)
      }
      case _ => throw new Exception("Incompatible Types")
    }
  }
}

case class VariableExpression(value : String) extends Expression {
  override def apply(ctx : ExecutionContext) : Expression = {
    ctx.dereference(value) match {
      case i : Int => NumericExpression(i)
      case d : Double => NumericExpression(d)
      case s : String => StringExpression(s)
      case true => True
      case false => False
      case v => Variable(v)
    }
  }
}

sealed case class Variable(value : Any) extends Expression {
  override def apply(ctx : ExecutionContext) : Any = value
}

sealed case class ValueExpression(value : Any) extends Expression {
  override def apply(ctx : ExecutionContext) : Expression = {
    value match {
      case i : Int => NumericExpression(i)
      case d : Double => NumericExpression(d)
      case s : String => StringExpression(s)
      case true => True
      case false => False
      case a : Any => Variable(a)
    }
  }
}

case class StringExpression(value : String) extends Expression {
  override def apply(ctx : ExecutionContext) : String = value.replaceAll("\"", "")
}

object True extends BooleanExpression {
  override def apply(ctx : ExecutionContext) : Boolean = true
}

object False extends BooleanExpression {
  override def apply(ctx : ExecutionContext) : Boolean = false
}

case class NumericExpression(value : Double) extends Expression {
  override def apply(ctx : ExecutionContext) : Double = value
}

case class MemberAccessExpression(value : Expression, member : String) extends Expression{
  override def apply(ctx : ExecutionContext) : Expression = {
    value(ctx) match {
      case v : Variable => {
        val obj = v.apply(ctx)
        val clazz = obj.getClass
        val field = clazz.getDeclaredField(member);
        field.setAccessible(true)
        val result = field.get(obj)
        Variable(result)
      }
    }
  }
}

case class MemberInvocationExpression(value : Expression, member : String, arguments : Option[ArgumentListExpression]) extends Expression {
  import scala.reflect.runtime.{universe => ru}
  
  override def apply(ctx : ExecutionContext) : Expression = {
    val args = if(arguments.isDefined) Some(arguments.get(ctx)) else None
    val argTypes = if(args.isDefined) args.map(arg => getTypeTag(arg).tpe) else None
    
    value(ctx) match {
      case v : Variable => {
        val obj = v.apply(ctx)
        val objMirror = ru.runtimeMirror(obj.getClass.getClassLoader()).reflect(obj)
        val method = objMirror.symbol.typeSignature.member(ru.newTermName(member))
        val result = objMirror.reflectMethod(method.asMethod)(args.getOrElse(List[Any]()) :_*)
        Variable(result)
      }
    }
  }
  
  def getTypeTag[T: ru.TypeTag](obj: T) = ru.typeTag[T]
}

case class ArgumentListExpression(rest : Option[ArgumentListExpression], expr : Expression) extends Expression {
  override def apply(ctx : ExecutionContext) : List[Any] = {
    if(rest.isDefined) expr(ctx) :: rest.get(ctx) else List[Any](expr(ctx))
  }
}
