package com.tam.cobol_interpreter.test.tools

import java.io.ByteArrayInputStream

import com.tam.cobol_interpreter.tools.InputStreamTool
import org.junit.runner.RunWith
import org.scalatest.FlatSpec
import org.scalatest.Matchers._
import org.scalatest.junit.JUnitRunner

/**
 * Created by tamu on 1/9/15.
 */
@RunWith(classOf[JUnitRunner])
class TestInputStreamTool extends FlatSpec{
  "InputStreamTool" should "read all bytes of an input stream" in {
    val bis = new ByteArrayInputStream(Array('0'.toByte, '1'.toByte, '2'.toByte))
    InputStreamTool.read(bis) should equal ("012")
  }
}
