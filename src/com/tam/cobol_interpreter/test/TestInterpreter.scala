package com.tam.cobol_interpreter.test

import java.io.{StringBufferInputStream, ByteArrayOutputStream}

import com.tam.cobol_interpreter.test.resource.FileResources
import com.tam.cobol_interpreter.tools.ByteArrayTool
import org.scalatest.FlatSpec
import org.scalatest.Matchers._
import com.tam.cobol_interpreter.InterpreterFactory

/**
 * Created by tamu on 1/6/15.
 */
class TestInterpreter extends FlatSpec{
  "Interpreter" should "interpret a cobol stream" in {
    val interpreter = InterpreterFactory.createInterpreter(
      FileResources.parserSchemas.test1(),
      FileResources.writerSchemas.test1()
    )
    val fOut = new ByteArrayOutputStream()
    val fIn = FileResources.cobol.test1()
    interpreter.interpret(fIn, fOut)
    ByteArrayTool.makeString(fOut.toByteArray) should equal (
      "123|211|XYZ\n456|-211|ABC")
  }

  "Interpreter" should "handle persistency and switching" in {
    val interpreter = InterpreterFactory.createInterpreter(
      FileResources.parserSchemas.test2(),
      FileResources.writerSchemas.test2()
    )
    val fOut = new ByteArrayOutputStream()
    val fIn = FileResources.cobol.test2()
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
      FileResources.parserSchemas.test2(),
      FileResources.writerSchemas.test3()
    )
    val fOut = new ByteArrayOutputStream()
    val fIn = FileResources.cobol.test2()
    interpreter.interpret(fIn, fOut)
    ByteArrayTool.makeString(fOut.toByteArray).trim() should equal (
      """
        '1|1|2
        '2|2|3
      """.stripMargin('\'').trim())
  }

  "Interpreter" should "have changeable terminators" in {
    val interpreter = InterpreterFactory.createInterpreter(
      FileResources.parserSchemas.test2(),
      FileResources.writerSchemas.test3()
    )
    val fOut = new ByteArrayOutputStream()
    val fIn = FileResources.cobol.test2()
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
