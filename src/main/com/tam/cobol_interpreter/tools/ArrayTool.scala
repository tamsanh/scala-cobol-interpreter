package com.tam.cobol_interpreter.tools

import scala.reflect.ClassTag

/**
 * Created by tamu on 1/5/15.
 */
object ArrayTool {
  def deepEquals[T:ClassTag](arr1: Array[T], arr2: Array[T]): Boolean =
    java.util.Arrays.deepEquals(arr1.asInstanceOf[Array[AnyRef]], arr2.asInstanceOf[Array[AnyRef]])

  def rectangularizeDoubleArray[T:ClassTag](byteArray: Array[Array[T]]): Array[Array[T]] = {
    val longestArrayLength = byteArray.foldLeft(0)((x, y) => x max y.length)
    byteArray.
      map({(colData:Array[T]) =>
      if(colData.length < longestArrayLength) {
        val newData = new Array[T](longestArrayLength)
        var i = 0
        while(i < longestArrayLength)
          for(j <- 0 until colData.length) {
            newData(i) = colData(j)
            i += 1
          }
        newData
      } else
        colData
    })
  }
}
