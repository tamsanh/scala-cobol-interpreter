package com.tam.cobol_interpreter.writer.schema

import java.io.InputStream

import com.tam.cobol_interpreter.tools.InputStreamTool
import com.tam.cobol_interpreter.writer.schema.expressions.WriterColumnExpressionGenerator

/**
 * Created by tamu on 1/5/15.
 */
object WriterSchemaFactory {
  def createSchema(schemaStream:InputStream): WriterSchema =
    createSchema(InputStreamTool.read(schemaStream))

  def createSchema(schemaString:String): WriterSchema = {
    val expressionList = WriterColumnExpressionGenerator.generateExpressionList(schemaString)
    new WriterSchema(expressionList)
  }
}
