package com.tam.cobol_interpreter.test.tools

import com.tam.cobol_interpreter.tools.ByteArrayTool
import org.junit.runner.RunWith
import org.scalatest.FlatSpec
import org.scalatest.Matchers._
import org.scalatest.junit.JUnitRunner

/**
 * Created by tamu on 1/4/15.
 */
@RunWith(classOf[JUnitRunner])
class TestByteArrayTool extends FlatSpec{

  "ByteArrayTool" should "turn a byte array to a string" in {
    ByteArrayTool.byteArrayToString(Array('h','i')) should equal ("Bytes(h,i)")
  }
  "ByteArrayTool" should "put question marks for unknown chars" in {
    ByteArrayTool.byteArrayToString(
      Array(0x0F:Byte, 0x0F:Byte)
    ) should equal ("Bytes(?,?)")
  }
}
