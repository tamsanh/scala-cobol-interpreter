package com.tam.cobol_interpreter.test.parser

import com.tam.cobol_interpreter.context.ParseContextFactory
import com.tam.cobol_interpreter.parser.branch.ParseBranch
import com.tam.cobol_interpreter.parser.branch.builder.{ParseBranchBuilder, StandardParseBranchBuilder, StringParseBranchBuilder}
import org.junit.runner.RunWith
import org.scalatest.FlatSpec
import org.scalatest.Matchers._
import org.scalatest.junit.JUnitRunner

/**
 * Created by tamu on 1/2/15.
 */
@RunWith(classOf[JUnitRunner])
class TestParseBranchBuilder extends FlatSpec {

  def testBuilder(builder: ParseBranchBuilder):ParseBranch = {
    builder.initializeParseBranch()
    builder.addIntNode("Field1", 3)
    builder.addComp3Node("Field2", 2)
    builder.addCharNode("Field3", 3)
    builder.setParseContext(ParseContextFactory.createParseContext(this.getClass.getResourceAsStream("/cobol/threeIntTwoComp3ThreeCharBytes.cobol")))
    builder.getParseBranch
  }

  def testBuilder2(builder: ParseBranchBuilder): ParseBranch = {
    builder.initializeParseBranch()
    builder.addCharNode("Field1", 4)
    builder.setParseContext(ParseContextFactory.createParseContext(this.getClass.getResourceAsStream("/cobol/three0One3.cobol")))
    builder.getParseBranch
  }

  def testBuilder3(builder: ParseBranchBuilder): ParseBranch = {
    builder.initializeParseBranch()
    builder.addFillNode(1)
    builder.addIntNode("Field1", 3)
    builder.setParseContext(ParseContextFactory.createParseContext(this.getClass.getResourceAsStream("/cobol/three0One3.cobol")))
    builder.getParseBranch
  }

  "A StandardParseBranchBuilder" should "build a branch that can parse a ParseContext" in {
    val pbb = new StandardParseBranchBuilder()

    var data = testBuilder(pbb).parse()
    data("Field1") should equal (Array("10".toCharArray))
    data("Field2") should equal (Array("211".toCharArray))
    data("Field3") should equal (Array("XYZ".toCharArray))

    data = testBuilder2(pbb).parse()
    data("Field1") should equal (Array("0003".toCharArray))

    data = testBuilder3(pbb).parse()
    data("Field1") should equal (Array("3".toCharArray))
  }

  "A StringParseBranchBuilder" should "return the string version of a ParseBranch" in {
    val pbb = new StringParseBranchBuilder()

    testBuilder(pbb)
    pbb.getStringParseBranch should equal (
      s"""Initialized
        |Node: Int 'Field1' 3
        |Node: Comp3 'Field2' 2
        |Node: Char 'Field3' 3
        |ParseContext: ${ParseContextFactory.createParseContext(this.getClass.getResourceAsStream("/cobol/threeIntTwoComp3ThreeCharBytes.cobol")).length}
        |Built""".stripMargin)

    testBuilder2(pbb)
    pbb.getStringParseBranch should equal (
    s"""Initialized
       |Node: Char 'Field1' 4
       |ParseContext: ${ParseContextFactory.createParseContext(this.getClass.getResourceAsStream("/cobol/three0One3.cobol")).length}
       |Built""".stripMargin
    )

    testBuilder3(pbb)
    pbb.getStringParseBranch should equal (
    s"""Initialized
       |Node: Fill 'Filler' 1
       |Node: Int 'Field1' 3
       |ParseContext: ${ParseContextFactory.createParseContext(this.getClass.getResourceAsStream("/cobol/three0One3.cobol")).length}
       |Built""".stripMargin
    )
  }

  "StringParseBranchBuilder" should "return the current length of its branch" in {
    val pbb = new StringParseBranchBuilder()

    testBuilder(pbb)
    pbb.getCurrentLength should equal (8)
  }

  "StandardParseBranchBuilder" should "return the current length of its branch" in {
    val pbb = new StandardParseBranchBuilder()

    testBuilder(pbb)
    pbb.getCurrentLength should equal (8)
  }
}
