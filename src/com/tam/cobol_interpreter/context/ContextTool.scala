package com.tam.cobol_interpreter.context

import com.tam.cobol_interpreter.tools.ArrayTool

/**
 * Created by tamu on 1/5/15.
 */
object ContextTool {
  def defaultData: () => Array[Array[Byte]] = () => Array(new Array[Byte](0))
}
