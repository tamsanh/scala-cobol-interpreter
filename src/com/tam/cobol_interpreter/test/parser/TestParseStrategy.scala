package com.tam.cobol_interpreter.test.parser

import com.tam.cobol_interpreter.context.ParseContext
import com.tam.cobol_interpreter.parser.strategy.{CharStrategy, Comp3Strategy, FillStrategy, IntStrategy}
import com.tam.cobol_interpreter.test.resource.ParseContextResource
import org.scalatest.FlatSpec
import org.scalatest.Matchers._

/**
 * Created by tamu on 1/2/15.
 */
class TestParseStrategy extends FlatSpec {

  "An IntStrategy" should "parse an integer" in {
    val pc = new ParseContext(ParseContextResource.threeIntTwoComp3ThreeCharBytes)
    val is = IntStrategy
    is.parse(pc, 3) should equal ("10".toCharArray)

    val pc2 = ParseContextResource.generateOneThroughNineChar()
    is.parse(pc2, 3) should equal ("123".toCharArray)
  }

  "A CharStrategy" should "parse a char as it is" in {
    val pc = new ParseContext(ParseContextResource.threeIntTwoComp3ThreeCharBytes)
    pc.skip(5) should equal (5)
    val cs = CharStrategy
    cs.parse(pc, 3) should equal ("XYZ".toCharArray)

    val pc2 = new ParseContext(Array(0x1F:Byte))
    cs.parse(pc2, 1) should equal (Array(0x1F:Byte))
  }

  "A Comp3Strategy" should "parse and unpack bytes as an integer" in {
    val pc = new ParseContext(ParseContextResource.threeIntTwoComp3ThreeCharBytes)
    pc.skip(3) should equal (3)
    val c3s = Comp3Strategy
    c3s.parse(pc, 2) should equal ("211".toCharArray)
  }

  "A FillStrategy" should "only skip the number of bytes" in {
    val pc = new ParseContext(ParseContextResource.threeIntTwoComp3ThreeCharBytes)
    val fs = FillStrategy
    fs.parse(pc, 3) should equal ("".toCharArray)
    pc.pointer should equal (3)
  }
}
