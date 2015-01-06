package com.tam.cobol_interpreter.test.parser

import com.tam.cobol_interpreter.context.ParseContext
import com.tam.cobol_interpreter.parser.branch.builder._
import com.tam.cobol_interpreter.parser.exceptions.{SchemaException, SwitchCaseException}
import com.tam.cobol_interpreter.parser.schema.expressions._
import com.tam.cobol_interpreter.parser.schema.{ParserSchema, ParserSchemaFactory}
import com.tam.cobol_interpreter.test.resource.ParseContextResource
import com.tam.cobol_interpreter.tools.ByteArrayTool
import org.scalatest.FlatSpec
import org.scalatest.Matchers._

/**
 * Created by tamu on 1/4/15.
 */
class TestParserSchema extends FlatSpec {
  "SchemaFactory" should "generate a schema based on a string" in {
    val schema = ParserSchemaFactory.createSchema(
      s"""
         |Test
         |Field1 Int 1
         |1
       """.stripMargin)
    schema should equal (new ParserSchema(
      Array(TableName("Test"), Column("Field1", "Int", "1"), RowBytes("1"))
    ))
  }
  "ParserSchema" should "generate a ParseBranch based on a Schema" in {
    val pc = ParseContextResource.generateOneThroughNineChar()
    val pbb = new StringParseBranchBuilder()
    val schema = ParserSchemaFactory.createSchema(
      s"""
         |Test
         |Field1 Int 9
         |9
       """.stripMargin)
    schema.build(pc, pbb)
    pbb.getStringParseBranch should equal (
      """Initialized
        |Node: Int 'Field1' 9
        |ParseContext: 9
        |Built
      """.stripMargin.trim()
    )
  }

  "ParserSchema" should "switch properly" in {
    val pc = ParseContextResource.generateOneThroughNineChar()
    val pbb = new StringParseBranchBuilder()
    val schema = ParserSchemaFactory.createSchema(
      s"""
         |Test
         |Switch Field1 Int 1
         |Case 1
         |Case1 Int 1
         |EndCase
         |Case 2
         |Case2 Int 1
         |EndCase
         |1
       """.stripMargin)
    schema.build(pc, pbb)
    pbb.getStringParseBranch should equal (
      s"""Initialized
         |Node: Int 'Case1' 1
         |ParseContext: 9
         |Built
       """.stripMargin.trim
    )
    pc.skip(1)
    schema.build(pc, pbb)
    pbb.getStringParseBranch should equal (
      s"""Initialized
         |Node: Int 'Case2' 1
         |ParseContext: 9
         |Built
       """.stripMargin.trim
    )
  }

  "ParserSchema" should "expand occurs correctly" in {
    val pc = ParseContextResource.generateOneThroughNineChar()
    val pbb = new StringParseBranchBuilder()
    val schema = ParserSchemaFactory.createSchema(
      s"""
         |Test
         |Occurs 3
         |Field1 Int 1
         |Filler 1
         |EndOccurs
         |6
       """.stripMargin)
    schema.build(pc, pbb)
    pbb.getStringParseBranch should equal (
      s"""Initialized
         |Node: Int 'Field1' 1
         |Node: Fill 'Filler' 1
         |Node: Int 'Field1' 1
         |Node: Fill 'Filler' 1
         |Node: Int 'Field1' 1
         |Node: Fill 'Filler' 1
         |ParseContext: 9
         |Built
       """.stripMargin.trim
    )
  }

  "ParserSchema" should "throw exceptions" in {
    val pc = ParseContextResource.generateOneThroughNineChar()
    val pbb = new StringParseBranchBuilder()
    val thrown = intercept[SwitchCaseException]{
      ParserSchemaFactory.createSchema(
        s"""
         |Test
         |Switch Field1 Int 1
         |Case -1
         |EndCase
         |0
       """.stripMargin).build(pc, new StringParseBranchBuilder())
    }
    thrown.getMessage should equal ("No Matching Case for Switch Val: 'Bytes(1)'")

    val thrown2 = intercept[SwitchCaseException] {
      ParserSchemaFactory.createSchema(
        s"""
         |Test
         |Switch Field1 Int 1
         |Case 1
         |EndCase
         |Case 1
         |EndCase
         |0
       """.stripMargin).build(pc, pbb)}
    thrown2.getMessage should equal ("Too Many Matching Cases for Switch Val: 'Bytes(1)'")
  }

  "ParserSchema" should "properly combine occurs and switches" in {
    val pc = ParseContextResource.generateOneThroughNineChar()
    val pbb = new StringParseBranchBuilder()
    val schema = ParserSchemaFactory.createSchema(
      s"""
         |Test
         |Occurs 3
         |Switch s Int 1
         |Case 1
         |Case1 Int 1
         |EndCase
         |Case 2
         |Case2 Int 1
         |EndCase
         |Case 3
         |Case3 Int 1
         |EndCase
         |EndOccurs
         |3
       """.stripMargin)
    schema.build(pc, pbb)
    pbb.getStringParseBranch should equal (
      s"""Initialized
         |Node: Int 'Case1' 1
         |Node: Int 'Case2' 1
         |Node: Int 'Case3' 1
         |ParseContext: 9
         |Built
       """.stripMargin.trim
    )
  }
  "ParserSchema" should "check schema bytes to be sure they're accurate" in {
    var thrown = intercept[SchemaException]{ParserSchemaFactory.createSchema(s"""
       |Test
       |Occurs 3
       |Field Int 3
       |EndOccurs
       |10
     """.stripMargin)}
    thrown.getMessage should equal ("Actual Size [9] does not equal RowByte Size [10]")

    thrown = intercept[SchemaException]{ParserSchemaFactory.createSchema(s"""
       |Test
       |Occurs 3
       |Switch s Int 1
       |Case 0
       |Field Int 3
       |EndCase
       |Case 2
       |Field2 Int 3
       |EndCase
       |EndOccurs
       |Field Int 5
       |10
     """.stripMargin)}
    thrown.getMessage should equal ("Actual Size [14] does not equal RowByte Size [10]")
  }

  "ParseSchemaFactory" should "catch unknown expressions" in {
    val thrown = intercept[SchemaException] {
      ParserSchemaFactory.createSchema( s"""
      |Test
      |I don't know this
      |1
    """.stripMargin)
    }
    thrown.getMessage should equal("Unrecognized ParserSchemaExpression: 'I don't know this'")
  }

  "ParseSchema" should "support a default value for cases" in {
    val schema = ParserSchemaFactory.createSchema( s"""
    'TestTable
    'Switch V Comp3 1
    'Case 1 | Case1 Comp3 1 | EndCase
    'Case -7 | Case3 Comp3 1 | EndCase
    'Case _ | Case2 Comp3 1 | EndCase
    'EndCase
    '1
  """.stripMargin('\''))
    val pbb = new StringParseBranchBuilder()
    val pc = new ParseContext(Array(0x1C.toByte, 0x2C.toByte, 0x33.toByte, 0x1D.toByte, 0x7D.toByte):Array[Byte])

    schema.build(pc, pbb)
    pbb.getStringParseBranch should equal (
      """Initialized
        |Node: Comp3 'Case1' 1
        |ParseContext: 5
        |Built
      """.stripMargin.trim())

    pc.skip(1)
    schema.build(pc, pbb)
    pbb.getStringParseBranch should equal (
      """Initialized
        |Node: Comp3 'Case2' 1
        |ParseContext: 5
        |Built
      """.stripMargin.trim())

    pc.skip(1)
    schema.build(pc, pbb)
    pbb.getStringParseBranch should equal (
      """Initialized
        |Node: Comp3 'Case2' 1
        |ParseContext: 5
        |Built
      """.stripMargin.trim())

    pc.skip(1)
    schema.build(pc, pbb)
    pbb.getStringParseBranch should equal (
      """Initialized
        |Node: Comp3 'Case2' 1
        |ParseContext: 5
        |Built
      """.stripMargin.trim())

    pc.skip(1)
    schema.build(pc, pbb)
    pbb.getStringParseBranch should equal (
      """Initialized
        |Node: Comp3 'Case3' 1
        |ParseContext: 5
        |Built
      """.stripMargin.trim())
  }

  "ParserSchema" should "compare case values using CharStrategy" in {
    val schema = ParserSchemaFactory.createSchema( s"""
    'TestTable
    'Switch V Comp3 1
    'Case 1 | Case1 Comp3 1 | EndCase
    'Case 2 | Case2 Comp3 1 | EndCase
    'Case -1 | Case3 Comp3 1 | EndCase
    'EndCase
    '1
  """.stripMargin('\''))
    val pbb = new StringParseBranchBuilder()
    val pc = new ParseContext(Array(0x1C.toByte, 0x2C.toByte, 0x1D.toByte, 0x2C.toByte):Array[Byte])

    schema.build(pc, pbb)
    pbb.getStringParseBranch should equal (
      """Initialized
        |Node: Comp3 'Case1' 1
        |ParseContext: 4
        |Built
      """.stripMargin.trim())

    pc.skip(1)
    schema.build(pc, pbb)
    pbb.getStringParseBranch should equal (
      """Initialized
        |Node: Comp3 'Case2' 1
        |ParseContext: 4
        |Built
      """.stripMargin.trim())

    pc.skip(1)
    schema.build(pc, pbb)
    pbb.getStringParseBranch should equal (
      """Initialized
        |Node: Comp3 'Case3' 1
        |ParseContext: 4
        |Built
      """.stripMargin.trim())

    pc.skip(1)
    schema.build(pc, pbb)
    pbb.getStringParseBranch should equal (
      """Initialized
        |Node: Comp3 'Case2' 1
        |ParseContext: 4
        |Built
      """.stripMargin.trim())
  }
  "ParserSchema" should "compare case values using CharStrategy preserving spaces" in {
    val schema = ParserSchemaFactory.createSchema( s"""
    'TestTable
    'Switch V Char 3
    'Case 0 1 | Case1 Char 3 | EndCase
    'Case 010 | Case2 Char 3 | EndCase
    'Case 100 | Case3 Char 3 | EndCase
    'EndCase
    '3
  """.stripMargin('\''))
    val pbb = new StringParseBranchBuilder()
    val pc = new ParseContext(ByteArrayTool.stringToByteArray("0 1010100"))

    schema.build(pc, pbb)
    pbb.getStringParseBranch should equal (
      """Initialized
        |Node: Char 'Case1' 3
        |ParseContext: 9
        |Built
      """.stripMargin.trim())

    pc.skip(3)
    schema.build(pc, pbb)
    pbb.getStringParseBranch should equal (
      """Initialized
        |Node: Char 'Case2' 3
        |ParseContext: 9
        |Built
      """.stripMargin.trim())

    pc.skip(3)
    schema.build(pc, pbb)
    pbb.getStringParseBranch should equal (
      """Initialized
        |Node: Char 'Case3' 3
        |ParseContext: 9
        |Built
      """.stripMargin.trim())

  }

  "ParserSchema" should "compare case values using CharStrategy preserving 0" in {
    val schema = ParserSchemaFactory.createSchema( s"""
    'TestTable
    'Switch V Char 3
    'Case 001 | Case1 Char 3 | EndCase
    'Case 010 | Case2 Char 3 | EndCase
    'Case 100 | Case3 Char 3 | EndCase
    'EndCase
    '3
  """.stripMargin('\''))
    val pbb = new StringParseBranchBuilder()
    val pc = new ParseContext(ByteArrayTool.stringToByteArray("001010100"))

    schema.build(pc, pbb)
    pbb.getStringParseBranch should equal (
      """Initialized
        |Node: Char 'Case1' 3
        |ParseContext: 9
        |Built
      """.stripMargin.trim())

    pc.skip(3)
    schema.build(pc, pbb)
    pbb.getStringParseBranch should equal (
      """Initialized
        |Node: Char 'Case2' 3
        |ParseContext: 9
        |Built
      """.stripMargin.trim())

    pc.skip(3)
    schema.build(pc, pbb)
    pbb.getStringParseBranch should equal (
      """Initialized
        |Node: Char 'Case3' 3
        |ParseContext: 9
        |Built
      """.stripMargin.trim())
  }

  "ParseSchemaFactory" should "catch more than one RowByte" in {
    intercept[SchemaException] {
      new ParserSchema(Array(RowBytes("1"), RowBytes("2")))
    }.getMessage should equal ("Too Many RowBytes Found")
    intercept[SchemaException] {
      new ParserSchema(Array(TableName("one")))
    }.getMessage should equal ("No RowByte Found")
  }
}
