package com.tam.cobol_interpreter.parser.schema

import com.tam.cobol_interpreter.context.ParseContext
import com.tam.cobol_interpreter.parser.branch.ParseBranch
import com.tam.cobol_interpreter.parser.branch.builder.ParseBranchBuilder
import com.tam.cobol_interpreter.parser.exceptions.{SchemaException, SwitchCaseException}
import com.tam.cobol_interpreter.parser.schema.expressions._
import com.tam.cobol_interpreter.parser.strategy.ParseStrategyFactory
import com.tam.cobol_interpreter.parser.strategy.CharStrategy
import com.tam.cobol_interpreter.tools.{ArrayTool, ByteArrayTool}

/**
 * Created by tamu on 1/4/15.
 */

class ParserSchema(expressionList: Array[ParserSchemaExpression]){
  /**
   * Schemas are the ones that are given the parseContexts, and build branches
   */
  def getExpressionList: Array[ParserSchemaExpression] = this.expressionList
  override def equals(that: Any): Boolean = {
    that match {
      case other:ParserSchema =>
        ArrayTool.deepEquals[ParserSchemaExpression](other.getExpressionList, this.getExpressionList)
    }
  }

  def build(parseContext: ParseContext, parseBranchBuilder: ParseBranchBuilder): ParseBranch = {
    val currPointer = parseContext.pointer
    parseBranchBuilder.initializeParseBranch()
    try {
      ParserSchema.recBuild(parseContext, parseBranchBuilder, this.expressionList)
      parseBranchBuilder.setParseContext(parseContext)
      parseBranchBuilder.getParseBranch
    } catch {
      case e:Exception =>
        parseBranchBuilder.initializeParseBranch()
        throw e
    } finally {
      parseContext.pointer = currPointer
    }
  }

  def actualSize: Int = expressionList.filter(!_.isInstanceOf[RowBytes]).map(_.bytes).sum

  def rowByteSize: Int = {
    val possibleRowBytes = expressionList.filter(_.isInstanceOf[RowBytes])
    possibleRowBytes.length match {
      case 1 => possibleRowBytes.head.bytes
      case 0 => throw new SchemaException("No RowByte Found")
      case _ => throw new SchemaException("Too Many RowBytes Found")
    }
  }

  def size: Int = this.actualSize

  def isSizeAccurate:Boolean = {
    this.actualSize == this.rowByteSize
  }

  if(!isSizeAccurate) throw new SchemaException(s"Actual Size [$actualSize] does not equal RowByte Size [$rowByteSize]")
}

object ParserSchema {
  def recBuild(parseContext:ParseContext, parseBranchBuilder: ParseBranchBuilder, schemaExpressions: Array[ParserSchemaExpression]): Unit = {
    if(schemaExpressions.length == 0)
      ()
    else
      schemaExpressions.head match {
        case _:TableName =>
          recBuild(parseContext, parseBranchBuilder, schemaExpressions.tail)
        case x:Column =>
          parseContext.skip(x.bytes)
          parseBranchBuilder.addNode(x.columnName, x.bytes, x.columnType)
          recBuild(parseContext, parseBranchBuilder, schemaExpressions.tail)
        case x:Filler =>
          parseContext.skip(x.bytes)
          parseBranchBuilder.addFillNode(x.bytes)
          recBuild(parseContext, parseBranchBuilder, schemaExpressions.tail)
        case x: Switch =>
          val switchStrategy = ParseStrategyFactory.getStrategy(x.column.columnType)
          val switchBytes:Array[Byte] = switchStrategy.parse(parseContext, x.column.bytes)
          parseContext.rewind(x.column.bytes)
          val charStrategy = new CharStrategy()
          val filteredCases = x.cases.filter({k =>
            java.util.Arrays.equals(
              charStrategy.parse(k.switchVal, k.switchVal.length),
              switchBytes)
          })
          val targetCase:Case =
            filteredCases.length match {
              case 1 => filteredCases.head
              case 0 =>
                val defaultCases = x.cases.filter(_.switchVal.head == '_'.toByte)
                defaultCases.length match {
                  case 1 => defaultCases.head
                  case 0 =>
                    val msg = s"No Matching Case for Switch Val: '${ByteArrayTool.byteArrayToString(switchBytes)}'"
                    throw new SwitchCaseException(msg, parseContext.pointer, switchBytes, x.column.columnName)
                  case _ =>
                    val msg = s"Multiple Default Cases for Switch ${x.column.columnName}"
                    throw new SwitchCaseException(msg, parseContext.pointer, switchBytes, x.column.columnName)
                }
              case _ =>
                val msg = s"Too Many Matching Cases for Switch Val: '${ByteArrayTool.byteArrayToString(switchBytes)}'"
                throw new SwitchCaseException(msg, parseContext.pointer, switchBytes, x.column.columnName)
            }
          recBuild(parseContext, parseBranchBuilder, targetCase.caseExpressions)
          recBuild(parseContext, parseBranchBuilder, schemaExpressions.tail)
        case x: Occurs =>
          for(i <- 0 until x.getCount) {
            recBuild(parseContext, parseBranchBuilder, x.occursExpressions)
          }
          recBuild(parseContext, parseBranchBuilder, schemaExpressions.tail)
        case _: RowBytes =>
          recBuild(parseContext, parseBranchBuilder, schemaExpressions.tail)
      }
  }

}
