package com.tam.cobol_interpreter.context

import java.io.{DataInputStream, InputStream}

import com.tam.cobol_interpreter.tools.ByteArrayTool

/**
 * Created by tamu on 1/1/15.
 */
class ParseContext(data: Array[Byte], size: Int) {
  /**
   * This is the context which will hold the data to be parsed.
   * The idea is that it will contain only enough data for a given row.
   * Maybe it can have data for an entire set of data?
   * We'd need to add grammar for how much data the ParseContext should hold..
   */
  var pointer = 0


  def this(arr: Array[Byte]) = this(arr, arr.size)
  def this(size: Int) = this(new Array[Byte](size), size)

  def getData = this.data
  def length = size

  def read(f: InputStream): Int = f.read(this.data, 0, this.size)

  def read(b: Array[Byte]): Unit = {
    this.read(b, 0)
  }

  def read(b: Array[Byte], offset: Int): Unit = {
    for(i <- 0 until size){
      this.data(i) = b(i + offset)
    }
  }

  def incrementAndGetNextByte(): Byte = {
    incrementPointer(1)
    this.data(pointer - 1)
  }

  def incrementPointer(bytes:Int):Int = {
    pointer += bytes
    assert(pointer <= size, "Pointer is Out of Bounds")
    pointer
  }

  def getNextBytes(bytes: Int): Array[Byte] = {
    (for( i <- 0 until bytes) yield incrementAndGetNextByte()).toArray
  }

  def rewind(bytes: Int): Int = {
    pointer = (pointer - bytes) max 0
    pointer
  }

  def skip(bytes: Int): Int = {
    incrementPointer(bytes)
    pointer
  }

  def resetPointer(): Unit = {
    this.pointer = 0
  }

  override def toString: String = s"ParseContext($pointer,$size,${ByteArrayTool.byteArrayToString(data)})"
}
