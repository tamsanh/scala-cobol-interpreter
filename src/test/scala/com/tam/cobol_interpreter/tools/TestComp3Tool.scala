package com.tam.cobol_interpreter.test.tools

import com.tam.cobol_interpreter.tools.Comp3Tool
import org.junit.runner.RunWith
import org.scalatest.FlatSpec
import org.scalatest.Matchers._
import org.scalatest.junit.JUnitRunner

/**
 * Created by tamu on 1/2/15.
 */
@RunWith(classOf[JUnitRunner])
class TestComp3Tool extends FlatSpec {
  "A Comp3Tool" should "expand packed bytes" in {
    Comp3Tool.unpack(Array(0x1D:Byte)) should equal ("-1".toCharArray)
    Comp3Tool.unpack(Array(0x1C:Byte)) should equal ("1".toCharArray)
    Comp3Tool.unpack(Array(0x02:Byte, 0x1C: Byte)) should equal ("21".toCharArray)
    Comp3Tool.unpack(Array(0x42:Byte, 0x1C: Byte)) should equal ("421".toCharArray)
    Comp3Tool.unpack(Array(0x42:Byte, 0x1D: Byte)) should equal ("-421".toCharArray)
  }

  "A Comp3Tool" should "pack integers" in {
    Comp3Tool.pack("-123") should equal (Array(0x12, 0x3D))
    Comp3Tool.pack("123") should equal (Array(0x12, 0x3C))
    Comp3Tool.pack("1323") should equal (Array(0x01, 0x32, 0x3C))
    Comp3Tool.pack("01323") should equal (Array(0x01, 0x32, 0x3C))
    Comp3Tool.pack("-01323") should equal (Array(0x01, 0x32, 0x3D))
    Comp3Tool.pack("11323") should equal (Array(0x11, 0x32, 0x3C))
    Comp3Tool.pack(11323) should equal (Array(0x11, 0x32, 0x3C))
    Comp3Tool.pack(-1323) should equal (Array(0x01, 0x32, 0x3D))
  }

  "A Comp3Tool" should "pack integers into requested sizes" in {
    Comp3Tool.pack(11323, 4) should equal (Array(0x00, 0x11, 0x32, 0x3C))
    Comp3Tool.pack(-1323, 10).length should equal (10)
    Comp3Tool.pack(11323, 3) should equal (Array(0x11, 0x32, 0x3C))
    intercept[AssertionError]{Comp3Tool.pack(11323, 2)}.getClass should equal (new AssertionError().getClass)
  }
}
