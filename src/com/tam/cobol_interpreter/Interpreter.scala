package com.tam.cobol_interpreter

import java.io._

import com.tam.cobol_interpreter.parser.ParserFactory
import com.tam.cobol_interpreter.parser.schema.ParserSchema
import com.tam.cobol_interpreter.writer.schema.WriterSchema
import com.tam.cobol_interpreter.writer.WriterFactory

/**
 * Created by tamu on 1/6/15.
 */
class Interpreter(parserSchema: ParserSchema, writerSchema: WriterSchema) {
  val writerFactory = new WriterFactory()

  def setQuote(q:Char):Interpreter = {writerFactory.setQuote(q); this}
  def setTerminator(q:Char):Interpreter = {writerFactory.setTerminator(q); this}
  def setSeparator(q:Char):Interpreter = {writerFactory.setSeparator(q); this}
  def setWriteHeader(b:Boolean):Interpreter = {writerFactory.setWriteHeader(b); this}

  def interpret(fIn: InputStream, fOut:OutputStream): Unit = {
    val parser = ParserFactory.createParser(parserSchema, fIn)
    val writer = this.writerFactory.createWriter(writerSchema, fOut)
    writer.write(parser)
  }

}

object Interpreter{
  def main (args:Array[String]): Unit ={
    args.length match {
      case 2 =>
        val parserSchemaFile = new File(args(0))
        val writerSchemaFile = new File(args(1))
        val interpreter = InterpreterFactory.createInterpreter(parserSchemaFile, writerSchemaFile)
        interpreter.interpret(System.in, System.out)
      case 4 =>
        val parserSchemaFile = new File(args(0))
        val writerSchemaFile = new File(args(1))
        val interpreter = InterpreterFactory.createInterpreter(parserSchemaFile, writerSchemaFile)
        interpreter.interpret(new FileInputStream(args(2)), new FileOutputStream(args(3)))
      case 3 =>
        val parserSchemaFile = new File(args(0))
        val writerSchemaFile = new File(args(1))
        val interpreter = InterpreterFactory.createInterpreter(parserSchemaFile, writerSchemaFile)
        interpreter.interpret(new FileInputStream(args(2)), System.out)
      case _ =>
        val usage =
          """
            |interpreter [Parser Schema Location] [Writer Schema Location]
            | Reads from stdin and writes to stdout
            |interpreter [Parser Schema Location] [Writer Schema Location] [Input File Location] [Output File Location]
          """.stripMargin.trim()
        System.out.print(usage + "\n")
    }
  }
}
