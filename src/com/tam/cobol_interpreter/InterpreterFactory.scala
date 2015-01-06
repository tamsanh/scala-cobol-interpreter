package com.tam.cobol_interpreter

import com.tam.cobol_interpreter.parser.schema.{ParserSchema, ParserSchemaFactory}
import com.tam.cobol_interpreter.writer.schema.{WriterSchema, WriterSchemaFactory}
import java.io.{FileInputStream, File, InputStream}

/**
 * Created by tamu on 1/6/15.
 */
object InterpreterFactory {
  def createInterpreter(parserSchemaFile:File, writerSchemaFile:File): Interpreter =
    createInterpreter(new FileInputStream(parserSchemaFile),
                      new FileInputStream(writerSchemaFile))
  def createInterpreter(parserSchemaStream:InputStream, writerSchemaStream:InputStream): Interpreter =
    createInterpreter(ParserSchemaFactory.createSchema(parserSchemaStream),
                      WriterSchemaFactory.createSchema(writerSchemaStream))
  def createInterpreter(parserSchemaString:String, writerSchemaString:String): Interpreter =
    createInterpreter(ParserSchemaFactory.createSchema(parserSchemaString),
                      WriterSchemaFactory.createSchema(writerSchemaString))
  def createInterpreter(parserSchema:ParserSchema, writerSchema:WriterSchema):Interpreter =
    new Interpreter(parserSchema, writerSchema)
}
