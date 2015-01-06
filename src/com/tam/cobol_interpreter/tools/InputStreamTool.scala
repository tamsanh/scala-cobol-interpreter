package com.tam.cobol_interpreter.tools

import java.io.InputStream

/**
 * Created by tamu on 1/6/15.
 */

object InputStreamTool {
  def read(is:InputStream):String = {
    val arr = new Array[Byte](is.available())
    is.read(arr)
    arr.map(_.toChar).mkString
  }
}
