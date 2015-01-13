package com.tam.cobol_interpreter.parser.schema

import java.io.InputStream

import com.tam.cobol_interpreter.parser.schema.expressions.{ExpressionGenerator, ParserSchemaExpression}
import com.tam.cobol_interpreter.tools.InputStreamTool

/**
 * Created by tamu on 1/4/15.
 */
object ParserSchemaFactory {
  def createSchema(schemaStream: InputStream):ParserSchema =
    createSchema(InputStreamTool.read(schemaStream))
  def createSchema(schemaString: String): ParserSchema =
    createSchema(ExpressionGenerator.generateExpressionTree(schemaString))
  def createSchema(expressionList: Array[ParserSchemaExpression]): ParserSchema =
    new ParserSchema(expressionList)
}
