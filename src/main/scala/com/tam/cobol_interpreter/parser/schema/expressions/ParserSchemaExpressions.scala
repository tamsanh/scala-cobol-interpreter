package com.tam.cobol_interpreter.parser.schema.expressions

import com.tam.cobol_interpreter.parser.exceptions._
import com.tam.cobol_interpreter.parser.strategy.CharStrategy
import com.tam.cobol_interpreter.tools.{ArrayTool, ByteArrayTool}

import scala.collection.mutable

/**
 * Created by tamu on 1/3/15.
 */
abstract class ParserSchemaExpression(inBytes:Int) {
  val bytes = inBytes
}
case class TableName(name: String) extends ParserSchemaExpression(0)
case class Column(columnName:String, columnType:String, columnBytes:String) extends ParserSchemaExpression(columnBytes.toInt)
case class Filler(fillerBytes:String) extends ParserSchemaExpression(fillerBytes.toInt)
case class Switch(column:Column, cases:Array[Case]) extends ParserSchemaExpression(cases(0).bytes) {
  final val defaultCaseValue = "_"
  override def equals(that:Any): Boolean = {
    that match {
      case other: Switch =>
        other.column == this.column &&
        ArrayTool.deepEquals[Case](other.cases, this.cases)
      case _ => false
    }
  }
  override def toString:String = s"Switch($column,Cases(${cases.map(_.toString).mkString(",\n")}))"

  val casesByValue = new mutable.HashMap[String, Case]()
  for(c <- cases) {
    //TODO: Using strings from byte arrays feels like a bad idea
    val parsedSwitch = ByteArrayTool.makeString(CharStrategy.parse(c.switchVal))
    if(casesByValue.contains(parsedSwitch))
      if (parsedSwitch == defaultCaseValue)
        throw new MultipleDefaultCases(this.defaultCaseValue)
      else
        throw new MultipleCaseValues(parsedSwitch)
    else
      casesByValue.update(parsedSwitch, c)
  }
  val defaultCase:Option[Case] = casesByValue.get(defaultCaseValue)
}
case class Case(switchVal: Array[Byte], caseExpressions: Array[ParserSchemaExpression]) extends ParserSchemaExpression(caseExpressions.map(_.bytes).sum) {
  override def equals(that:Any): Boolean = {
    that match {
      case other: Case =>
        java.util.Arrays.equals(other.switchVal, this.switchVal) &&
        ArrayTool.deepEquals[ParserSchemaExpression](other.caseExpressions, this.caseExpressions)
      case _ => false
    }
  }
  override def toString:String = {
    s"Case(${ByteArrayTool.byteArrayToString(switchVal)},Expr(${caseExpressions.map(_.toString).mkString(",")}))"
  }
}
case class Occurs(count:String, occursExpressions:Array[ParserSchemaExpression]) extends ParserSchemaExpression(occursExpressions.map(_.bytes).sum * count.toInt) {
  def getCount:Int = count.toInt
  override def equals(that:Any): Boolean = {
    that match {
      case other: Occurs =>
        other.count == this.count &&
        ArrayTool.deepEquals[ParserSchemaExpression](other.occursExpressions, this.occursExpressions)
      case _ => false
    }
  }
  override def toString:String = s"Occurs($count,Expr(${occursExpressions.map(_.toString).mkString(",")}))"
}
case class RowBytes(rowBytes:String) extends ParserSchemaExpression(rowBytes.toInt)

