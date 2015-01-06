package com.tam.cobol_interpreter.test.resource

import com.tam.cobol_interpreter.context.ParseContext

/**
 * Created by tamu on 1/2/15.
 */
object ParseContextResource {
  val generateOneThroughNineChar: () => ParseContext = { () =>
    val arr:Array[Byte] = (1 until 10).mkString.map(_.toByte).toArray
    new ParseContext(arr)
  }

  val threeIntTwoComp3ThreeCharBytes = Array('0'.toByte, '1'.toByte, '0'.toByte, 0x21: Byte, 0x1C: Byte, 'X': Byte, 'Y': Byte, 'Z': Byte)
  val generateThreeIntTwoComp3ThreeChar: () => ParseContext = { () =>
    new ParseContext(threeIntTwoComp3ThreeCharBytes)
  }

  val generateThree0One3: () => ParseContext = { () =>
    new ParseContext(Array('0'.toByte, '0'.toByte, '0'.toByte, '3'.toByte))
  }
}
