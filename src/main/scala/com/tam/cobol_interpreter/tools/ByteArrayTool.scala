package com.tam.cobol_interpreter.tools

/**
 * Created by tamu on 1/4/15.
 */
object ByteArrayTool {
  def byteToString(k:Byte):String = if (32 <= k && k <= 126 ) k.toChar.toString else "?"
  def byteArrayToString(arr: Array[Byte]): String =
    "Bytes(" + arr.map(byteToString).mkString(",") + ")"
  def makeString(arr:Array[Byte]):String = arr.map(_.toChar).mkString
  def stringToByteArray(s:String): Array[Byte] = s.toCharArray.map(_.toByte).toArray

}
