package com.tam.cobol_interpreter.test.parser

import com.tam.cobol_interpreter.parser.branch.ParseBranch
import com.tam.cobol_interpreter.parser.exceptions.ParseNodeException
import com.tam.cobol_interpreter.parser.strategy.{FillStrategy, IntStrategy}
import com.tam.cobol_interpreter.test.resource.ParseContextResource
import com.tam.cobol_interpreter.tools.ByteArrayTool
import org.scalatest.FlatSpec
import org.scalatest.Matchers._

/**
 * Created by tamu on 1/2/15.
 */
class TestParseBranch extends FlatSpec {
  "A ParseBranch" should "parse a parse context and assign the value to a DataContext" in {
    val pc = ParseContextResource.generateOneThroughNineChar()
    val pb = new ParseBranch(pc)
    pb.addNode("Field1", 1, new IntStrategy)
    pb.addNode("Field2", 2, new IntStrategy)
    (4 until 10).map(k => pb.addNode("Field3", 1, new IntStrategy))
    val data = pb.parse()

    data("Field1") should equal (Array("1".toCharArray))
    data("Field2") should equal (Array("23".toCharArray))
    data("Field3") should equal ((4 until 10).map(_.toString.toCharArray).toArray)
  }

  "A ParseBranch" should "throw an exception if there is no ParseContext" in {
    val pb = new ParseBranch()
    val thrown = intercept[Exception] {
      pb.getParseContext
    }
    thrown.getMessage should equal ("No ParseContext Set")
  }

  "ParseBranch" should "properly identify bytes which cause exceptions" in {
    val pc = ParseContextResource.generateThreeIntTwoComp3ThreeChar()
    val pb = new ParseBranch(pc)
    pb.addNode("Filler", 5, new FillStrategy)
    pb.addNode("BadInt", 3, new IntStrategy)
    val thrown = intercept[ParseNodeException]{pb.parse()}
    thrown.getClass.getName should equal ("com.tam.cobol_interpreter.parser.exceptions.ParseNodeException")
    thrown.getNodeName should equal ("BadInt")
    ByteArrayTool.byteArrayToString(thrown.getReadBytes) should equal ("Bytes(X,Y,Z)")
  }
}
