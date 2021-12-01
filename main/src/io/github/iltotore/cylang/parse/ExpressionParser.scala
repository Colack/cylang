package io.github.iltotore.cylang.parse

import io.github.iltotore.cylang.ast.Expression.*
import io.github.iltotore.cylang.ast.{Expression, Value}

import scala.util.parsing.combinator.*

object ExpressionParser extends RegexParsers {

  def expression: Parser[Expression] = variableAssignment

  def empty = success(Empty)

  //Literal
  def bool: Parser[Literal] = raw"(true)|(false)".r ^^ { x => Literal(Value.Bool(x.toBoolean)) }

  def text: Parser[Literal] = "\\\".*\\\"".r ^^ { x => Literal(Value.Text(x.substring(1, x.length - 1))) }

  def character: Parser[Literal] = raw"'.'".r ^^ { x => Literal(Value.Character(x.charAt(1))) }

  def real: Parser[Literal] = raw"[0-9]+\.[0-9]+".r ^^ { x => Literal(Value.Real(x.toDouble)) }

  def integer: Parser[Literal] = raw"[0-9]+".r ^^ { x => Literal(Value.Integer(x.toLong)) }

  def literalSymbol = bool | text | character | real | integer

  //Misc
  def paranthesized = "(" ~> expression <~ ")"

  def variableCall = raw"\w+".r ^^ VariableCall.apply

  def functionCall = raw"\w+".r ~ ("(" ~> repsep(expression, ",") <~ ")") ^^ {
    case name ~ args => FunctionCall(name, args)
  }

  private val binaryOps: Map[String, (Expression, Expression) => Expression] = Map(
    "=" -> Equality.apply,
    ">" -> Greater.apply,
    ">=" -> GreaterEqual.apply,
    "<" -> Less.apply,
    "<=" -> LessEqual.apply,
    "+" -> Addition.apply,
    "-" -> Substraction.apply,
    "*" -> Multiplication.apply,
    "/" -> Division.apply,
    "DIV" -> WholeDivision.apply,
    "%" -> Modulo.apply
  )

  //Binary Operators
  //POUR i DE 0 A 10 FAIRE
  def forLoop: Parser[ForLoop] = "POUR" ~> raw"\w+".r ~ "DE" ~ expression ~ "A" ~ expression ~ ("PAS DE" ~> expression).? ~ "FAIRE" ~ tree("FIN POUR") ^^ {
    case param ~ "DE" ~ from ~ "A" ~ to ~ Some(step) ~ "FAIRE" ~ expr => ForLoop(param, from, to, step, expr)
    case param ~ "DE" ~ from ~ "A" ~ to ~ None ~ "FAIRE" ~ expr => ForLoop(param, from, to, Literal(Value.Integer(1)), expr)
  }

  def whileLoop: Parser[WhileLoop] = "TANT QUE" ~> expression ~ "FAIRE" ~ tree("FIN TANT QUE") ^^ { case cond ~ "FAIRE" ~ expr => WhileLoop(cond, expr) }

  def ifElse: Parser[If] = "SI" ~> expression ~ "FAIRE" ~ ifBody ^^ {
    case cond ~ "FAIRE" ~ (expr ~ elseExpr) => If(cond, expr, elseExpr)
  }

  def ifBody: Parser[~[Expression, Expression]] =  (tree("SINON") ~ (ifElse | ("FAIRE" ~> tree("FIN SI")))) | (tree("FIN SI") ~ empty)
  
  def treeReturn = "RETOURNER" ~> expression ^^ Return.apply

  def treeInvocable = forLoop | whileLoop | ifElse | treeReturn

  def tree(end: Parser[?]) = rep(not(end) ~> (expression | treeInvocable)) <~ end ^^ Tree.apply

  def variableAssignment = equality | (raw"\w+".r ~ "<-" ~ equality ^^ { case name ~ _ ~ expr => VariableAssignment(name, expr) })

  def equality = inequality * ("!?=".r ^^ binaryOps.apply)

  def inequality = arith * ("[<>]=?".r ^^ binaryOps.apply)

  def arith = term * ("[+-]".r ^^ binaryOps.apply)

  def term = unary * ("[*/%]|(DIV)".r ^^ binaryOps.apply)

  private val unaryOps: Map[String, Expression => Expression] = Map(
    "+" -> (x => x),
    "-" -> Negation.apply,
    "!" -> Not.apply
  )

  def unary = raw"[+\-!]".r.? ~ invocable ^^ {
    case Some(op) ~ expr => unaryOps(op)(expr)

    case None ~ expr => expr
  }

  def invocable = literalSymbol | paranthesized | functionCall | variableCall

}