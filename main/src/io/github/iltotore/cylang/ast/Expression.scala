package io.github.iltotore.cylang.ast

import io.github.iltotore.cylang.eval.EvaluationError
import io.github.iltotore.cylang.{CYType, Context, Parameter, Scope, Variable}

sealed trait Expression

object Expression {
  
  case object Empty extends Expression
  
  case class Literal(value: Value) extends Expression

  case class Negation(expression: Expression) extends Expression

  case class Addition(left: Expression, right: Expression) extends Expression

  case class Subtraction(left: Expression, right: Expression) extends Expression

  case class Multiplication(left: Expression, right: Expression) extends Expression

  case class Division(left: Expression, right: Expression) extends Expression

  case class WholeDivision(left: Expression, right: Expression) extends Expression
  
  case class Modulo(left: Expression, right: Expression) extends Expression
  
  case class Equality(left: Expression, right: Expression) extends Expression
  
  case class Greater(left: Expression, right: Expression) extends Expression
  
  case class GreaterEqual(left: Expression, right: Expression) extends Expression

  case class Less(left: Expression, right: Expression) extends Expression

  case class LessEqual(left: Expression, right: Expression) extends Expression

  case class Not(expression: Expression) extends Expression
  
  case class And(left: Expression, right: Expression) extends Expression
  
  case class Or(left: Expression, right: Expression) extends Expression

  case class VariableCall(name: String) extends Expression

  case class VariableAssignment(name: String, expression: Expression) extends Expression

  case class ArrayCall(arrayExpr: Expression, index: Expression) extends Expression

  case class ArrayAssignment(arrayExpr: Expression, index: Expression, expression: Expression) extends Expression

  case class StructureCall(structureExpr: Expression, name: String) extends Expression
  
  case class StructureAssignment(structureExpr: Expression, name: String, expression: Expression) extends Expression
  
  case class FunctionCall(name: String, args: List[Expression]) extends Expression

  case class ForLoop(name: String, from: Expression, to: Expression, step: Expression, expression: Expression) extends Expression

  case class WhileLoop(condition: Expression, expression: Expression) extends Expression

  case class DoWhileLoop(condition: Expression, expression: Expression) extends Expression
  
  case class If(condition: Expression, expression: Expression, elseExpression: Expression) extends Expression

  case class Tree(expressions: List[Expression]) extends Expression
  
  case class Return(expression: Expression) extends Expression

  case class Body(variables: List[Parameter], expression: Expression)
  
  sealed trait Declaration extends Expression
  
  case class ConstantDeclaration(name: String, expression: Expression) extends Declaration

  case class EnumerationDeclaration(name: String, fields: List[String]) extends Declaration
  
  case class StructureDeclaration(name: String, fields: List[Parameter]) extends Declaration

  case class FunctionDeclaration(name: String, tpe: CYType, parameters: List[Parameter], body: Body) extends Declaration

  case class ProgramDeclaration(name: String, declarations: List[Declaration], body: Body) extends Expression
}