package com.tam.cobol_interpreter.parser

import java.io.InputStream

import com.tam.cobol_interpreter.context.{DataContext, ParseContext}
import com.tam.cobol_interpreter.parser.branch.builder.StandardParseBranchBuilder
import com.tam.cobol_interpreter.parser.exceptions._
import com.tam.cobol_interpreter.parser.schema.ParserSchema
import com.tam.cobol_interpreter.tools.ByteArrayTool

/**
 * Created by tamu on 1/4/15.
 */
class Parser(schema:ParserSchema, fileToParse:InputStream) extends java.util.Iterator[DataContext] {

  val totalBytes = fileToParse.available()
  var totalBytesRead = 0

  override def next(): DataContext = {
    val parseContext = new ParseContext(schema.size)
    totalBytesRead += parseContext.read(fileToParse)
    val parseBranchBuilder = new StandardParseBranchBuilder()
    try {
      val parseBranch = schema.build(parseContext, parseBranchBuilder)
      parseBranch.parse()
    } catch {
      case e:ParseNodeException =>
        val badBytePos = (e.pointerPosition - e.readBytes.length) + (totalBytesRead - parseContext.length)
        val msg = s"Node:'${e.nodeName}' Position:$badBytePos Bytes:'${ByteArrayTool.makeString(e.readBytes)}' Msg:'${e.getMessage}'"
        throw new ParserException(msg)
      case e:SwitchCaseException =>
        val badBytePos = (e.pointerPosition - e.readBytes.length) + (totalBytesRead - parseContext.length)
        val msg = s"Node:'${e.nodeName}' Position:$badBytePos Bytes:'${ByteArrayTool.makeString(e.readBytes)}' Msg:'${e.getMessage}'"
        throw new ParserException(msg)
      case e:Exception => throw e
    }
  }

  override def hasNext: Boolean =
    this.fileToParse.available() >= schema.size

  override def remove(): Unit = throw new UnsupportedOperationException()
}
