package com.tam.cobol_interpreter.writer

import java.io.OutputStream

import com.tam.cobol_interpreter.context.{DataContext, WriteContext}
import com.tam.cobol_interpreter.tools.{ArrayTool, ByteArrayTool}
import com.tam.cobol_interpreter.writer.schema.WriterSchema

/**
 * Created by tamu on 1/5/15.
 */
object Writer {
  val quoteEscape = '\\'.toByte
  def quotedData(oldData:Array[Byte], quoteByte:Byte):Array[Byte] = {
    val newLength = oldData.length + oldData.filter(_ == quoteByte).length + 2
    val newArray = new Array[Byte](newLength)
    newArray(0) = quoteByte
    var newI = 1
    var oldI = 0
    while (newI < newLength - 1) {
      val newVal =
        if(oldData(oldI) == quoteByte) {
          newArray(newI) = quoteEscape
          newI += 1
          oldData(oldI)
        }
        else
          oldData(oldI)
      newArray(newI) = newVal
      oldI += 1
      newI += 1
    }
    newArray(newLength - 1) = quoteByte
    newArray
  }
}
class Writer(writerSchema:WriterSchema, fOut:OutputStream, writeHeader:Boolean, quote:Char, sep:Char, term:Char) {

  val quoteByte = quote.toByte
  val sepByte = Array(sep.toByte)
  val termByte = Array(term.toByte)

  val quoteList = Array(this.quote.toByte, this.sep.toByte, this.term.toByte)

  var firstRow = true

  def writeField(writableData:Array[Byte]): Unit =
    if(writableData.foldLeft(false)((a, b) => a || quoteList.contains(b)))
      fOut.write(Writer.quotedData(writableData, this.quote.toByte))
    else
      fOut.write(writableData)

  def writeLineTerm():Unit = fOut.write(this.termByte)
  def writeFieldTerm():Unit = fOut.write(this.sepByte)

  def writeRow(writableData:Array[Array[Array[Byte]]]):Unit = {
    // This feels grimy. Please don't tell anyone about it
    if(!this.firstRow)
      writeLineTerm()
    else {
      this.firstRow = false
    }

    val rectData = ArrayTool.rectangularizeDoubleArray[Array[Byte]](writableData)
    for(rowIndex <- 0 until rectData(0).length){
      if(rowIndex > 0)
        writeLineTerm()
      writeField(rectData.head(rowIndex))
      for(column <- rectData.drop(1)){
        writeFieldTerm()
        writeField(column(rowIndex))
      }
    }
  }
  
  def writeWithContext(dataContext: DataContext, writeContext: WriteContext):Unit = {
    this.writerSchema.updateWriteContext(dataContext, writeContext)
    if(writeContext.isWritable)
      writeRow(writeContext.getOrderedColumnData)
  }
  
  def writeHeaderRow():Unit = {
    val headerRow:Array[Array[Array[Byte]]] = this.writerSchema.getColumns.map(k => Array(ByteArrayTool.stringToByteArray(k)))
    this.writeRow(headerRow)
  }
  
  def write(dataContextIterator:java.util.Iterator[DataContext]):Unit = {
    val writeContext = new WriteContext()
    if(this.writeHeader)
      this.writeHeaderRow()

    while(dataContextIterator.hasNext){
      val dataContext = dataContextIterator.next()
      this.writeWithContext(dataContext, writeContext)
    }
  }
}
