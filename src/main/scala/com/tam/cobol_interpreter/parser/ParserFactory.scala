package com.tam.cobol_interpreter.parser

import java.io.InputStream

import com.tam.cobol_interpreter.parser.schema.{ParserSchema, ParserSchemaFactory}
import com.tam.cobol_interpreter.tools.InputStreamTool

/**
 * Created by tamu on 1/6/15.
 */
object ParserFactory {

  def createParser(schemaFile:InputStream, toParse:InputStream): Parser =
    createParser(InputStreamTool.read(schemaFile), toParse)

  def createParser(schemaString:String, toParse:InputStream):Parser =
    createParser(ParserSchemaFactory.createSchema(schemaString), toParse)

  def createParser(parserSchema:ParserSchema, toParse:InputStream):Parser =
    new Parser(parserSchema, toParse)
}
