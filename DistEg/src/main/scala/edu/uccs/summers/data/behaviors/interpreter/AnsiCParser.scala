package edu.uccs.summers.data.behaviors.interpreter

import scala.util.parsing.combinator.JavaTokenParsers

class AnsiCParser extends JavaTokenParsers {
  // Adapted from http://www.lysator.liu.se/c/ANSI-C-grammar-y.html
  
  def primaryExpression : Parser[Any] = (
      ident
    | constant
    | stringLiteral
    | "(" ~ expression ~ ")"
  )
  
  def postfixExpression : Parser[Any] = (
      primaryExpression
    | postfixExpression ~ "[" ~ expression ~ "]"
    | postfixExpression ~ "(" ~ ")"
    | postfixExpression ~ "(" ~ argumentExpressionList ~ ")"
    | postfixExpression ~ "." ~ ident
  )
  
  def argumentExpressionList : Parser[Any] = (
      assignmentExpression
    | argumentExpressionList ~ "," ~ assignmentExpression
  )
  
  def unaryExpression : Parser[Any] = (
      postfixExpression
    | unaryOperator ~ unaryExpression
  )
  
  def unaryOperator = (
      "+"
    | "-"
    | "~"
  )
  
  def multiplicativeExpression : Parser[Any] = (
      unaryExpression
    | multiplicativeExpression ~ "*" ~ multiplicativeExpression
    | multiplicativeExpression ~ "/" ~ multiplicativeExpression
    | multiplicativeExpression ~ "%" ~ multiplicativeExpression 
  )
  
  def additiveExpression : Parser[Any] = (
      multiplicativeExpression
    | additiveExpression ~ "+" ~ additiveExpression
    | additiveExpression ~ "-" ~ additiveExpression
  )
  
  def relationalExpression : Parser[Any] = (
      additiveExpression
    | relationalExpression ~ "<" ~ additiveExpression
    | relationalExpression ~ ">" ~ additiveExpression
    | relationalExpression ~ "<=" ~ additiveExpression
    | relationalExpression ~ ">=" ~ additiveExpression
  )
  
  def equalityExpression : Parser[Any] = (
      relationalExpression
    | equalityExpression ~ "==" ~ relationalExpression
    | equalityExpression ~ "!=" ~ relationalExpression
  )
  
  def logicalAndExpression : Parser[Any] = (
      equalityExpression
    | logicalAndExpression ~ "&&" ~ equalityExpression
  )
  
  def logicalOrExpression : Parser[Any] = (
      logicalAndExpression
    | logicalOrExpression ~ "||" ~ logicalAndExpression
  )
  
  def assignmentExpression : Parser[Any] = (
      logicalOrExpression
    | unaryExpression ~ assignmentOperator ~ assignmentExpression
  )
  
  def assignmentOperator = (
      "="
    | "*="
    | "/="
    | "+="
    | "-="
  )

  def expression : Parser[Any] = (
      assignmentExpression
    | assignmentExpression ~ "," ~ expression
  )
  
  def declaration = (
      declarationSpecifiers ~ ";"
    | declarationSpecifiers ~ initDeclaratorList ~ ";"
  )
  
  def declarationSpecifiers = (
      typeSpecifiers
  )
  
  def initDeclaratorList : Parser[Any] = (
      initDeclarator
    | initDeclaratorList ~ "," ~ initDeclarator
  )
  
  def initDeclarator = (
      declarator
    | declarator ~ "=" ~ initializer
  )
  
  def declarator : Parser[Any] = (
      ident
    | "(" ~ declarator ~ ")"
    | declarator ~ "[" ~ logicalOrExpression ~ "]"
    | declarator ~ "[" ~ "]"
    | declarator ~ "(" ~ parameterTypeList ~ ")"
    | declarator ~ "(" ~ identifierList ~ ")"
    | declarator ~ "(" ~ ")"
  )
  
  def parameterTypeList = (
      parameterDeclaration
    | parameterList ~ "," ~ parameterDeclaration
  )
  
  def parameterList : Parser[Any] = (
      parameterDeclaration
    | parameterList ~ "," ~ parameterDeclaration
  )
  
  def parameterDeclaration = (
      declarationSpecifiers ~ declarator
    | declarationSpecifiers ~ abstractDeclarator
    | declarationSpecifiers
  )
  
  def identifierList : Parser[Any] = (
      ident
    | identifierList ~ "," ~ ident
  )
  
  def abstractDeclarator : Parser[Any] = (
      "(" ~ abstractDeclarator ~ ")"
    | "[" ~ "]"
    | "[" ~ logicalOrExpression ~ "]"
    | abstractDeclarator ~ "[" ~ "]"
    | abstractDeclarator ~ "[" ~ logicalOrExpression ~ "]"
    | "(" ~ ")"
    | "(" ~ parameterTypeList ~ ")"
    | abstractDeclarator ~ "(" ~ ")"
    | abstractDeclarator ~ "(" ~ parameterTypeList ~ ")"
  )
  
  def initializer : Parser[Any] = (
      assignmentExpression
    | "{" ~ initializerList ~ "}"
    | "{" ~ initializerList ~ "," ~ "}"
  )
  
  def initializerList : Parser[Any] = (
      initializer
    | initializerList ~ "," ~ initializer
  )
  
  def statement : Parser[Any] = (
      compoundStatement
    | expressionStatement
    | selectionStatement
    | iterationStatement
    | jumpStatement
  )
  
  def compoundStatement = (
      "{" ~ "}"
    | "{" ~ statementList ~ "}"
    | "{" ~ declarationList ~ "}"
    | "{" ~ declarationList ~ statementList ~ "}"
  )
  
  def declarationList : Parser[Any]= (
      declaration
    | declarationList ~ declaration
  )
  
  def statementList : Parser[Any] = (
      statement
    | statementList ~ statement
  )
  
  def expressionStatement = (
      ";"
    | expression ~ ";"
  )
  
  def selectionStatement = (
      "if" ~ "(" ~ expression ~ ")" ~ statement
    | "if" ~ "(" ~ expression ~ ")" ~ statement ~ "else" ~ statement
  )
  
  def iterationStatement = (
      "while" ~ "(" ~ expression ~ ")" ~ statement 
    | "do" ~ statement ~ "while" ~ "(" ~ expression ~ ")" ~ ";"
    | "for" ~ "(" ~ expressionStatement ~ expressionStatement ~ ")" ~ statement
    | "for" ~ "(" ~ expressionStatement ~ expressionStatement ~ expression ~ ")" ~ statement
  )
  
  def jumpStatement = (
      "return" ~ ";"
    | "return" ~ expression ~ ";"
  )
  
  def translationUnit : Parser[Any] = (
      functionDefinition
    | translationUnit ~ functionDefinition
  )
  
  def functionDefinition = (
      declarationSpecifiers ~ declarator ~ declarationList ~ compoundStatement
    | declarationSpecifiers ~ declarator ~ compoundStatement
    | declarator ~ declarationList ~ compoundStatement
    | declarator ~ compoundStatement
  )
  def typeSpecifiers = (
      "int"
  )
  
  def constant = wholeNumber | decimalNumber | floatingPointNumber
}