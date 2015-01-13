package com.tam.cobol_interpreter.context

import java.io.InputStream

/**
 * Created by tamn on 1/13/15.
 */
object ParseContextFactory {
  def createParseContext(is:InputStream):ParseContext = {
    val byteData = new Array[Byte](is.available())
    is.read(byteData)
    new ParseContext(byteData)
  }
}
