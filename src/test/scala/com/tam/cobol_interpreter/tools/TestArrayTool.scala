package com.tam.cobol_interpreter.test.tools

import com.tam.cobol_interpreter.tools.ArrayTool
import org.junit.runner.RunWith
import org.scalatest.FlatSpec
import org.scalatest.Matchers._
import org.scalatest.junit.JUnitRunner

/**
 * Created by tamu on 1/5/15.
 */
@RunWith(classOf[JUnitRunner])
class TestArrayTool extends FlatSpec {
  "RectangleTool" should "make all arrays of byte arrays equal length" in {
    ArrayTool.rectangularizeDoubleArray[Array[Byte]](
      Array(
        Array(
          Array(0.toByte),
          Array(1.toByte)
        ),
        Array(
          Array(0.toByte)
        )
      )
    ) should equal(
      Array(
        Array(
          Array(0.toByte),
          Array(1.toByte)
        ),
        Array(
          Array(0.toByte),
          Array(0.toByte)
        )
      )
    )

    ArrayTool.rectangularizeDoubleArray[Array[Byte]](
      Array(
        Array(
          Array(0.toByte),
          Array(1.toByte),
          Array(3.toByte),
          Array(2.toByte)
        ),
        Array(
          Array(0.toByte),
          Array(1.toByte)
        )
      )
    ) should equal(
      Array(
        Array(
          Array(0.toByte),
          Array(1.toByte),
          Array(3.toByte),
          Array(2.toByte)
        ),
        Array(
          Array(0.toByte),
          Array(1.toByte),
          Array(0.toByte),
          Array(1.toByte)
        )
      )
    )
  }

}
