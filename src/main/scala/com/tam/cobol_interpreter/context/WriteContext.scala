package com.tam.cobol_interpreter.context

import com.tam.cobol_interpreter.tools.ArrayTool

import scala.collection.mutable

/**
 * Created by tamu on 1/5/15.
 */

class WriteContext {

  var data = mutable.HashMap[String, Array[Array[Byte]]]()
  var order = mutable.LinkedList[String]()

  var isWritable = true

  def columnDataExists(columnName:String):Boolean = order.contains(columnName)

  private def addColumnToOrder(columnName:String):Unit = {
    if(!columnDataExists(columnName))
      this.order = this.order :+ columnName
  }

  def isNewColumnData(columnName:String, newData:Array[Array[Byte]]):Boolean = {
    val oldData = this.getColumnData(columnName)
    !ArrayTool.deepEquals[Array[Byte]](newData, oldData) &&
      !ArrayTool.deepEquals[Array[Byte]](newData, ContextTool.defaultData())
  }

  def updateIfNewColumnData(columnName:String, data:Array[Array[Byte]]):Unit =
    if(isNewColumnData(columnName, data)) updateColumnData(columnName, data)

  def updateColumnData(columnName:String, data:Array[Array[Byte]]):Unit = {
    addColumnToOrder(columnName)
    this.data.update(columnName, data)
  }

  def getColumnData(columnName:String): Array[Array[Byte]] =
    this.data.getOrElse(columnName, ContextTool.defaultData())

  def getOrderedColumnData: Array[Array[Array[Byte]]] =
    this.order.map(getColumnData).toArray

}

