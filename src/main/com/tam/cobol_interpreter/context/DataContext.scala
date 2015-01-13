package com.tam.cobol_interpreter.context

import com.tam.cobol_interpreter.tools.ByteArrayTool

import scala.collection.mutable

/**
 * Created by tamu on 1/2/15.
 */
class DataContext extends mutable.HashMap[String, Array[Array[Byte]]]{
  def getData(key:String): Array[Array[Byte]] = this.getOrElse(key, ContextTool.defaultData())

  def add(key: String, value: String): Unit = this.add(key, ByteArrayTool.stringToByteArray(value))
  def add(key: String, value: Array[Byte]): Unit = {
    // Append the value to the array at the passed key
    val oldArray = this.getOrElse(key, new Array[Array[Byte]](0))
    val newArray = new Array[Array[Byte]](oldArray.length + 1)
    for(i <- 0 until oldArray.length)
      newArray(i) = oldArray(i)
    newArray(newArray.length - 1) = value
    this(key) = newArray
  }
}
