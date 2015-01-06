package com.tam.cobol_interpreter.test.parser

import java.io.File

import com.tam.cobol_interpreter.context.{ContextTool, DataContext}
import com.tam.cobol_interpreter.parser.{ParserFactory, Parser}
import com.tam.cobol_interpreter.test.resource.FileResources
import org.scalatest.FlatSpec
import org.scalatest.Matchers._

/**
 * Created by tamu on 1/4/15.
 */
class TestParser extends FlatSpec {
  "Parser" should "parse a file into DataContexts" in {
    // Field1 Int 3, Field2 Comp3 2, Field3 Char 3
    val schemaFile = FileResources.parserSchemas.test1()
    // 123, 211, XYZ | 456, -211, ABC
    val cobolFile = FileResources.cobol.test1()

    val p = ParserFactory.createParser(schemaFile, cobolFile)
    val dc1 = p.next()
    val dc2 = p.next()
    p.hasNext should equal (false)

    dc1("Field1")(0) should equal ("123".toCharArray)
    dc1("Field2")(0) should equal ("211".toCharArray)
    dc1("Field3")(0) should equal ("XYZ".toCharArray)

    dc2("Field1")(0) should equal ("456".toCharArray)
    dc2("Field2")(0) should equal ("-211".toCharArray)
    dc2("Field3")(0) should equal ("ABC".toCharArray)
  }

  "Parser" should "properly handle cases" in {
    // FieldS Int 1 Field1 Int 4
    // FieldS Int 1 Field2 Int 2 Field3 Int 3
    val schemaFile = FileResources.parserSchemas.test2()
    // 10001201021000220203
    val cobolFile = FileResources.cobol.test2()

    val p = ParserFactory.createParser(schemaFile, cobolFile)
    p.hasNext should equal (true)
    val dc1 = p.next()
    val dc2 = p.next()
    val dc3 = p.next()
    val dc4 = p.next()
    p.hasNext should equal (false)

    def fieldMap(dc: DataContext):Array[Array[Array[Byte]]] = {
      Array("FieldS", "Field1", "Field2", "Field3").map(dc.getData)
    }

    def toByteArray(arr:String*): Array[Array[Array[Byte]]] = arr.map({ k =>
      if(k == "")
        ContextTool.defaultData()
      else
        Array(k.toCharArray.map(_.toByte))
    }).toArray

    fieldMap(dc1) should equal (toByteArray("1", "1", "", ""))
    fieldMap(dc2) should equal (toByteArray("2", "", "1", "2"))
    fieldMap(dc3) should equal (toByteArray("1", "2", "", ""))
    fieldMap(dc4) should equal (toByteArray("2", "", "2", "3"))
  }

  "Parser" should "not be able to remove" in {
    val schemaFile = FileResources.parserSchemas.test2()
    val cobolFile = FileResources.cobol.test2()

    val p = ParserFactory.createParser(schemaFile, cobolFile)
    val thrown = intercept[UnsupportedOperationException]{p.remove()}
    thrown.getClass should equal (new UnsupportedOperationException().getClass)
  }

}
