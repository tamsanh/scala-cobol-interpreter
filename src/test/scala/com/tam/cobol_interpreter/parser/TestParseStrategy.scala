package com.tam.cobol_interpreter.test.parser

import java.io.InputStreamReader

import com.tam.cobol_interpreter.context.{ParseContext, ParseContextFactory}
import com.tam.cobol_interpreter.parser.strategy.{CharStrategy, Comp3Strategy, FillStrategy, IntStrategy}
import com.tam.cobol_interpreter.tools.ByteArrayTool
import org.junit.runner.RunWith
import org.scalatest.FlatSpec
import org.scalatest.Matchers._
import org.scalatest.junit.JUnitRunner

/**
 * Created by tamu on 1/2/15.
 */
@RunWith(classOf[JUnitRunner])
class TestParseStrategy extends FlatSpec {

  "An IntStrategy" should "parse an integer" in {
    val pc = ParseContextFactory.createParseContext(this.getClass.getResourceAsStream("/cobol/threeIntTwoComp3ThreeCharBytes.cobol"))
    val is = IntStrategy
    is.parse(pc, 3) should equal ("10".toCharArray)

    val pc2 = ParseContextFactory.createParseContext(this.getClass.getResourceAsStream("/cobol/oneThroughNine.cobol"))
    is.parse(pc2, 3) should equal ("123".toCharArray)
  }

  "IntStrategy" should "be able to parse a large integer" in {
    val pc = ParseContextFactory.createParseContext(this.getClass.getResourceAsStream("/cobol/twentyTwoDigitNumber.cobol"))
    val is = IntStrategy
    ByteArrayTool.makeString(is.parse(pc, pc.length)) should equal ("27000002940120399850")
  }

  "A CharStrategy" should "parse a char as it is" in {
    val pc = ParseContextFactory.createParseContext(this.getClass.getResourceAsStream("/cobol/threeIntTwoComp3ThreeCharBytes.cobol"))
    pc.skip(5) should equal (5)
    val cs = CharStrategy
    cs.parse(pc, 3) should equal ("XYZ".toCharArray)

    val pc2 = new ParseContext(Array(0x1F:Byte))
    cs.parse(pc2, 1) should equal (Array(0x1F:Byte))
  }

  "A Comp3Strategy" should "parse and unpack bytes as an integer" in {
    val pc = ParseContextFactory.createParseContext(this.getClass.getResourceAsStream("/cobol/threeIntTwoComp3ThreeCharBytes.cobol"))
    pc.skip(3) should equal (3)
    val c3s = Comp3Strategy
    c3s.parse(pc, 2) should equal ("211".toCharArray)
  }

  "A FillStrategy" should "only skip the number of bytes" in {
    val pc = ParseContextFactory.createParseContext(this.getClass.getResourceAsStream("/cobol/threeIntTwoComp3ThreeCharBytes.cobol"))
    val fs = FillStrategy
    fs.parse(pc, 3) should equal ("".toCharArray)
    pc.pointer should equal (3)
  }
}
