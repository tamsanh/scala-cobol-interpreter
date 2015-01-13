package com.tam.cobol_interpreter.writer

import java.io.{InputStream, OutputStream}

import com.tam.cobol_interpreter.tools.InputStreamTool
import com.tam.cobol_interpreter.writer.schema.{WriterSchema, WriterSchemaFactory}

/**
 * Created by tamu on 1/6/15.
 */
class WriterFactory {
  var sep = '|'
  var term = '\n'
  var quote = '"'
  var writeHeader = false

  def setQuote(q:Char):WriterFactory = {
    this.quote = q
    this
  }
  def setTerminator(q:Char):WriterFactory = {this.term = q; this}
  def setSeparator(q:Char):WriterFactory = {this.sep = q; this}
  def setWriteHeader(b:Boolean):WriterFactory = {this.writeHeader = b; this}

  def createWriter(schemaFile:InputStream, fOut:OutputStream):Writer =
    createWriter(InputStreamTool.read(schemaFile), fOut)

  def createWriter(schemaString:String, fOut:OutputStream):Writer =
    createWriter(WriterSchemaFactory.createSchema(schemaString), fOut)

  def createWriter(writerSchema:WriterSchema, fOut:OutputStream):Writer =
    new Writer(writerSchema, fOut, this.writeHeader, this.quote, this.sep, this.term)
}
