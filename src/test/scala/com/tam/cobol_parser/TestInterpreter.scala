package com.tam.cobol_interpreter.test

import java.io.{ByteArrayOutputStream, StringBufferInputStream}

import com.tam.cobol_interpreter.InterpreterFactory
import com.tam.cobol_interpreter.tools.ByteArrayTool
import org.scalatest.FlatSpec
import org.scalatest.Matchers._

/**
 * Created by tamu on 1/6/15.
 */
class TestInterpreter extends FlatSpec{
  "Interpreter" should "interpret a cobol stream" in {
    val interpreter = InterpreterFactory.createInterpreter(
      this.getClass.getResourceAsStream("/schemas/parserTest1.parser"),
      this.getClass.getResourceAsStream("/schemas/writerTest1.writer")
    )
    val fOut = new ByteArrayOutputStream()
    val fIn = this.getClass.getResourceAsStream("/cobol/cobolTest1.cobol")
    interpreter.interpret(fIn, fOut)
    ByteArrayTool.makeString(fOut.toByteArray) should equal (
      "123|211|XYZ\n456|-211|ABC")
  }

  "Interpreter" should "handle persistency and switching" in {
    val interpreter = InterpreterFactory.createInterpreter(
      this.getClass.getResourceAsStream("/schemas/parserTest2.parser"),
      this.getClass.getResourceAsStream("/schemas/writerTest2.writer")
    )
    val fOut = new ByteArrayOutputStream()
    val fIn = this.getClass.getResourceAsStream("/cobol/cobolTest2.cobol")
    interpreter.interpret(fIn, fOut)
    ByteArrayTool.makeString(fOut.toByteArray).trim() should equal (
      """
        '1|||1
        '1|1|2|2
        '2|||1
        '2|2|3|2
      """.stripMargin('\'').trim())
  }

  "Interpreter" should "be aware of non-empty fields" in {
    val interpreter = InterpreterFactory.createInterpreter(
      this.getClass.getResourceAsStream("/schemas/parserTest2.parser"),
      this.getClass.getResourceAsStream("/schemas/writerTest3.writer")
    )
    val fOut = new ByteArrayOutputStream()
    val fIn = this.getClass.getResourceAsStream("/cobol/cobolTest2.cobol")
    interpreter.interpret(fIn, fOut)
    ByteArrayTool.makeString(fOut.toByteArray).trim() should equal (
      """
        '1|1|2
        '2|2|3
      """.stripMargin('\'').trim())
  }

  "Interpreter" should "have changeable terminators" in {
    val interpreter = InterpreterFactory.createInterpreter(
      this.getClass.getResourceAsStream("/schemas/parserTest2.parser"),
      this.getClass.getResourceAsStream("/schemas/writerTest3.writer")
    )
    val fOut = new ByteArrayOutputStream()
    val fIn = this.getClass.getResourceAsStream("/cobol/cobolTest2.cobol")
    interpreter.setSeparator(',')
    interpreter.setTerminator('y')
    interpreter.interpret(fIn, fOut)
    ByteArrayTool.makeString(fOut.toByteArray) should equal (
      """
        '1,1,2y2,2,3
      """.stripMargin('\'').trim())
  }

  "Interpreter" should "respect quotes" in {
    val interpreter = InterpreterFactory.createInterpreter(
    "Field1 Char 3\n3",
    "Field1"
    )
    val fOut = new ByteArrayOutputStream()
    val fIn = new StringBufferInputStream("ABCXYZZ,3")
    interpreter.setSeparator('\0')
    interpreter.setTerminator(',')
    interpreter.setQuote('\'')
    interpreter.interpret(fIn, fOut)
    ByteArrayTool.makeString(fOut.toByteArray) should equal (
      """
        |ABC,XYZ,'Z,3'
      """.stripMargin('|').trim())
  }
}
