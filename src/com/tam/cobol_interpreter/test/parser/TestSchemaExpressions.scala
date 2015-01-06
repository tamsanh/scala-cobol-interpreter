package com.tam.cobol_interpreter.test.parser

import com.tam.cobol_interpreter.parser.schema.expressions._
import org.scalatest.FlatSpec
import org.scalatest.Matchers._

/**
 * Created by tamu on 1/4/15.
 */
class TestSchemaExpressions extends FlatSpec{

  "Filler" should "have proper bytes" in { Filler("3").bytes should equal (3) }

  "Cases" should "equal each other" in {
    new Case(new Array(0), Array(Column("one", "two", "3"))) should equal (
      Case(new Array(0), Array(Column("one", "two", "3"))))
    Case(new Array(0), Array(Column("one", "two", "3"))) should not equal
      Case(new Array(0), Array(Column("one", "three", "3")))
    Case(Array('1'.toByte), Array(Column("one", "two", "3"))) should not equal
      Case(new Array(0), Array(Column("one", "two", "3")))
    Case(new Array(0), Array(Column("one", "two", "3"))) should not equal "Rock"

  }

  "Cases" should "implement toString" in {
    Case(Array('0'.toByte), Array(Column("one", "three", "3"))).toString should equal(
      "Case(Bytes(0),Expr(Column(one,three,3)))"
    )
  }

  "Switches" should "equal each other" in {
    new Switch(Column("one", "two", "3"), Array(
      Case(new Array(0), Array(Column("one", "two", "3")))
    )) should equal (
      Switch(Column("one", "two", "3"), Array(
        Case(new Array(0), Array(Column("one", "two", "3")))
      ))
    )
    Switch(Column("one", "two", "3"), Array(
      Case(new Array(0), Array(Column("one", "two", "3")))
    )) should not equal "hi"

    Switch(Column("one", "two", "3"), Array(
      Case(new Array(0), Array(Column("one", "two", "3")))
    )) should not equal
      Switch(Column("one", "two", "4"), Array(
        Case(new Array(0), Array(Column("one", "two", "3")))
      ))
    Switch(Column("one", "two", "4"), Array(
      Case(new Array(0), Array(Column("one", "two", "3")))
    )) should not equal
      Switch(Column("one", "two", "4"), Array(
        Case(Array('2'.toByte), Array(Column("one", "two", "3")))
      ))
  }

  "Switches" should "implement toString" in {
    Switch(Column("one", "two", "3"), Array(
      Case(Array('0'.toByte), Array(Column("one", "two", "3")))
    )).toString should equal (
      "Switch(Column(one,two,3),Cases(Case(Bytes(0),Expr(Column(one,two,3)))))"
    )
  }

  "Occurs" should "calculate a proper sum" in {
    new Occurs("3", Array(Column("one", "two", "3"))).bytes should equal (9)
  }

  "Occurs" should "equal each other" in {
    Occurs("3", Array(Column("one", "two", "3"))) should equal (
      Occurs("3", Array(Column("one", "two", "3")))
    )

    Occurs("2", Array(Column("one", "two", "3"))) should not equal
      Occurs("3", Array(Column("one", "two", "3")))

    Occurs("3", Array(Column("one", "two", "1"))) should not equal
      Occurs("3", Array(Column("one", "two", "3")))


    Occurs("3", Array(Column("one", "two", "3"))) should not equal "Ex"
  }

  "Occurs" should "implement toString" in {
    Occurs("3", Array(Column("one", "two", "3"))).toString should equal ("Occurs(3,Expr(Column(one,two,3)))")
  }

  "Cases" should "have proper byte counts" in {
    new Case(new Array(0), Array(Column("one", "two", "3"))).bytes should equal (3)
  }

  "Occurs" should "have proper byte counts" in {
    Occurs("3", Array(Column("one", "two", "3"))).bytes should equal (9)
  }

  "RowBytes" should "keep the correct bytes" in {
    new RowBytes("3").bytes should equal (3)
    RowBytes("3").bytes should equal (3)
  }

  "TableName" should "keep its name" in {
    TableName("t").name should equal ("t")
    TableName("t").bytes should equal (0)
  }

  "Column" should "have all its parts available" in {
    new Column("1", "2", "3").columnName should equal ("1")
    Column("1", "2", "3").columnType should equal ("2")
    Column("1", "2", "3").columnBytes should equal ("3")
    Column("1", "2", "3").bytes should equal (3)
  }

  "Column" should "throw if it doesn't have a correct int" in {
    val thrown = intercept[NumberFormatException]{Column("1", "2", "a")}
    assert(thrown.isInstanceOf[NumberFormatException])
  }

  "Filler" should "have all its parts available" in {
    new Filler("3").bytes should equal (3)
    Filler("3").fillerBytes should equal ("3")
  }

  "Cases" should "support spaces" in {
    ("Case This Should Match        " match {
      case ExpressionMatcher.CaseMatcher(x) => x
    }) should equal ("This Should Match")
  }
}
