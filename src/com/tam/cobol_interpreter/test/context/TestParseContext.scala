package com.tam.cobol_interpreter.test.context

import java.io.StringBufferInputStream

import com.tam.cobol_interpreter.context.ParseContext
import org.scalatest.FlatSpec
import org.scalatest.Matchers._

/**
 * Created by tamu on 1/2/15.
 */
class TestParseContext extends FlatSpec {
  val toRead = Array(0x10: Byte, 0x1C: Byte, 'X': Byte)
  val test = new ParseContext(toRead.length)
  test.read(toRead)

  "A ParseContext" should "properly read a byte array" in {
    test.getData should equal (toRead)
  }

  "A ParseContext" should "properly reset its pointer" in {
    test.skip(3)
    test.resetPointer()
    test.pointer should equal (0)
  }

  "A ParseContext" should "correctly increment its pointer" in {
    test.getData should equal (toRead)

    test.pointer should equal (0)
    test.incrementAndGetNextByte()
    test.pointer should equal (1)
    test.incrementAndGetNextByte()
    test.pointer should equal (2)
    test.incrementAndGetNextByte()
    test.pointer should equal (3)

    test.resetPointer()
  }

  "A ParseContext" should "get multiple next bytes based on the number of bytes passed" in {
    test.getData should equal (toRead)

    test.getNextBytes(1) should equal (toRead slice(0, 1))
    test.resetPointer()
  }

  "A ParseContext" should "rewind correctly" in {
    test.pointer should equal (0)
    test.incrementAndGetNextByte()
    test.pointer should equal (1)
    test.rewind(1)
    test.pointer should equal (0)
    test.rewind(-1)
    test.pointer should equal (1)
    test.resetPointer()
  }

  "A ParseContext" should "skip correctly" in {
    test.pointer should equal (0)
    test.skip(1)
    test.pointer should equal (1)
    test.skip(-1)
    test.pointer should equal (0)
    test.resetPointer()
  }

  "A ParseContext" should "correctly read an InputStream" in {
    val pc = new ParseContext(3)
    pc.read(new StringBufferInputStream("hello"))
    pc.incrementAndGetNextByte() should equal ('h')
    pc.incrementAndGetNextByte() should equal ('e')
    pc.incrementAndGetNextByte() should equal ('l')
  }

  "A ParseContext" should "throw an AssertionError if it's going out of bounds" in {
    val pc = new ParseContext(new Array[Byte](0))
    val thrown = intercept[java.lang.AssertionError] {pc.incrementAndGetNextByte()}
    thrown.getMessage should equal ("assertion failed: Pointer is Out of Bounds")
  }

  "A ParseContext" should "have a length" in {
    val pc = new ParseContext(new Array[Byte](0))
    pc.length should equal (0)
  }

  "A ParseContext" should "implement toString" in {
    val pc = new ParseContext(Array('1'.toByte))
    pc.toString should equal ("ParseContext(0,1,Bytes(1))")
  }
}
