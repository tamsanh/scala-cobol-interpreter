package com.tam.cobol_interpreter.parser

import java.io.InputStream

import com.tam.cobol_interpreter.context.{DataContext, ParseContext}
import com.tam.cobol_interpreter.parser.branch.builder.StandardParseBranchBuilder
import com.tam.cobol_interpreter.parser.exceptions._
import com.tam.cobol_interpreter.parser.schema.ParserSchema

/**
 * Created by tamu on 1/4/15.
 */
class Parser(schema:ParserSchema, fileToParse:InputStream) extends java.util.Iterator[DataContext] {

  val totalBytes = fileToParse.available()
  var totalBytesRead = 0

  override def next(): DataContext = {
    val parseContext = new ParseContext(schema.rowByteSize)
    totalBytesRead += parseContext.read(fileToParse)
    val parseBranchBuilder = new StandardParseBranchBuilder()
    try {
      val parseBranch = schema.buildParseBranch(parseContext, parseBranchBuilder)
      parseBranch.parse()
    } catch {
      case e:ParseNodeException =>
        val badBytePos = totalBytesRead - e.getReadBytes.length
        throw new ParserException(e.getMessage, e.getNodeName, badBytePos, e.getReadBytes)
      case e:Exception => throw e
    }
  }

  override def hasNext: Boolean =
    this.fileToParse.available() >= schema.size

  override def remove(): Unit = throw new UnsupportedOperationException()
}
