package com.tam.cobol_interpreter.test.parser

import com.tam.cobol_interpreter.parser.schema.expressions._
import com.tam.cobol_interpreter.tools.ArrayTool
import org.scalatest.FlatSpec
import org.scalatest.Matchers._

/**
 * Created by tamu on 1/3/15.
 */

class TestExpressionGenerator extends FlatSpec {

  "ExpressionGenerator" should "generate a simple expression tree" in {
    ExpressionGenerator.generateExpressionTree(
      """Test_Table
        |Field1 Int 3
        |3""".stripMargin) should equal (
      Array(
        TableName("Test_Table"),
        Column("Field1", "Int", "3"),
        RowBytes("3")
      )
    )
  }

  "ExpressionGenerator" should "generate occurs" in {
    ExpressionGenerator.generateExpressionTree(
      """Test_Table
        |Occurs 3
        |Field1 Int 3
        |EndOccurs
        |9""".stripMargin) should equal (
      Array(
        TableName("Test_Table"),
        Occurs("3", Array(Column("Field1", "Int", "3"))),
        RowBytes("9")
      )
    )
  }

  "ExpressionGenerator" should "generate switch cases" in {
    val test = ExpressionGenerator.recGenerateExpressionTree(
      """
        |Switch switchField Int 1
        |Case 0
        |b Int 3
        |EndCase
        |Case 1
        |c Int 3
        |EndCase
      """.stripMargin.trim().split("\n"))._1
    val testSwitch = test(0).asInstanceOf[Switch]
    testSwitch.column should equal (Column("switchField", "Int", "1"))
    val firstCase = testSwitch.cases(0)
    firstCase.switchVal should equal (Array(48.toByte))
    firstCase.caseExpressions(0) should equal (Column("b", "Int", "3"))

    val secondCase = testSwitch.cases(1)
    secondCase.caseExpressions(0) should equal (Column("c", "Int", "3"))
    secondCase.switchVal should equal (Array(49.toByte))
  }

  "ExpressionGenerator" should "ignore comments" in {
    ExpressionGenerator.generateExpressionTree(
      """
        |# Ignore this comment
        |Test_Table
        |# Ignore this one too
        |Occurs 3
        |# Ignore comments here
        |Field1 Int 3
        |EndOccurs
        |# Ignore me as well
        |Switch Field2 Int 1
        |Case 0
        |# Ignore a comment right here
        |EndCase
        |# Ignore a comment right here too
        |9""".stripMargin) should equal (
      Array(
        TableName("Test_Table"),
        Occurs("3", Array(Column("Field1", "Int", "3"))),
        Switch(Column("Field2", "Int", "1"), Array(
          Case(Array('0':Byte), Array())
        )),
        RowBytes("9")
      )
    )
  }

  "ExpressionGenerator" should "count pipes as line terminators" in {
    ExpressionGenerator.generateExpressionTree(
      """
Test_Table
Switch switchField Int 1
Case 0
  a Int 1 | b Int 3 | x Int 4
EndCase
Case 1
  a Int 1 | c Int 3 | y Int 4
EndCase
8
      """) should equal (
      Array(
        TableName("Test_Table"),
        Switch(
          Column("switchField", "Int", "1"),
          Array(
            Case(Array('0'.toByte),
              Array(
                Column("a", "Int", "1"),
                Column("b", "Int", "3"),
                Column("x", "Int", "4")
              )
            ),
            Case(Array('1'.toByte),
              Array(
                Column("a", "Int", "1"),
                Column("c", "Int", "3"),
                Column("y", "Int", "4")
              )
            )
          )
        ),
        RowBytes("8")
      )
    )
  }

  "ExpressionGenerator" should "ignore empty lines" in {
    ExpressionGenerator.generateExpressionTree(
      """
Test_Table


Switch switchField Int 1
Case 0

  a Int 1 | b Int 3 | x Int 4
EndCase
Case 1
  a Int 1 | c Int 3 | y Int 4
EndCase



8
      """) should equal (
      Array(
        TableName("Test_Table"),
        Switch(
          Column("switchField", "Int", "1"),
          Array(
            Case(Array('0'.toByte),
              Array(
                Column("a", "Int", "1"),
                Column("b", "Int", "3"),
                Column("x", "Int", "4")
              )
            ),
            Case(Array('1'.toByte),
              Array(
                Column("a", "Int", "1"),
                Column("c", "Int", "3"),
                Column("y", "Int", "4")
              )
            )
          )
        ),
        RowBytes("8")
      )
    )
  }
}
