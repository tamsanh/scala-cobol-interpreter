package com.tam.cobol_interpreter.test.parser

import com.tam.cobol_interpreter.parser.branch.ParseBranch
import com.tam.cobol_interpreter.parser.branch.builder.{ParseBranchBuilder, StandardParseBranchBuilder, StringParseBranchBuilder}
import com.tam.cobol_interpreter.test.resource.ParseContextResource
import org.scalatest.FlatSpec
import org.scalatest.Matchers._

/**
 * Created by tamu on 1/2/15.
 */
class TestParseBranchBuilder extends FlatSpec {

  def testBuilder(builder: ParseBranchBuilder):ParseBranch = {
    builder.initializeParseBranch()
    builder.addIntNode("Field1", 3)
    builder.addComp3Node("Field2", 2)
    builder.addCharNode("Field3", 3)
    builder.setParseContext(ParseContextResource.generateThreeIntTwoComp3ThreeChar())
    builder.getParseBranch
  }

  def testBuilder2(builder: ParseBranchBuilder): ParseBranch = {
    builder.initializeParseBranch()
    builder.addCharNode("Field1", 4)
    builder.setParseContext(ParseContextResource.generateThree0One3())
    builder.getParseBranch
  }

  def testBuilder3(builder: ParseBranchBuilder): ParseBranch = {
    builder.initializeParseBranch()
    builder.addFillNode(1)
    builder.addIntNode("Field1", 3)
    builder.setParseContext(ParseContextResource.generateThree0One3())
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
        |ParseContext: ${ParseContextResource.generateThreeIntTwoComp3ThreeChar().length}
        |Built""".stripMargin)

    testBuilder2(pbb)
    pbb.getStringParseBranch should equal (
    s"""Initialized
       |Node: Char 'Field1' 4
       |ParseContext: ${ParseContextResource.generateThree0One3().length}
       |Built""".stripMargin
    )

    testBuilder3(pbb)
    pbb.getStringParseBranch should equal (
    s"""Initialized
       |Node: Fill 'Filler' 1
       |Node: Int 'Field1' 3
       |ParseContext: ${ParseContextResource.generateThree0One3().length}
       |Built""".stripMargin
    )
  }
}