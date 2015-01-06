package com.tam.cobol_interpreter.test.writer

import com.tam.cobol_interpreter.writer.schema.exceptions.WriterColumnException
import com.tam.cobol_interpreter.writer.schema.expressions.{NonEmptyColumn, PersistentColumn, BasicColumn, WriterColumnExpressionGenerator}
import org.scalatest.FlatSpec
import org.scalatest.Matchers._

/**
 * Created by tamu on 1/5/15.
 */
class TestWriterColumnExpressionGenerator extends FlatSpec{
  "WriterColumnExpressionGenerator" should "generate WriterColumnExpression lists" in {
    WriterColumnExpressionGenerator.generateExpressionList(
      """Field1
        |Field2
        |Field3 Persistent
        |Field4
      """.stripMargin) should equal (
      Array(
        BasicColumn("Field1"),
        BasicColumn("Field2"),
        PersistentColumn("Field3"),
        BasicColumn("Field4")
      )
    )
  }

  "WriterColumnExpressionGenerator" should "be cool with pipes in comments" in {
    WriterColumnExpressionGenerator.generateExpressionList(
      """Field1
        '# This is a comment
        'Field2
        '# This is a | in a comment
        'Field3 Persistent
        '# This is another | in a comment
        'Field4
      """.stripMargin('\'')) should equal (
      Array(
        BasicColumn("Field1"),
        BasicColumn("Field2"),
        PersistentColumn("Field3"),
        BasicColumn("Field4")
      )
    )
  }
  "WriterColumnExpressionGenerator" should "ignore comments" in {
    WriterColumnExpressionGenerator.generateExpressionList(
      """Field1
        |Field2
        |# This is a comment
        |Field3 Persistent
        |## This is also a comment
        |Field4
      """.stripMargin) should equal (
      Array(
        BasicColumn("Field1"),
        BasicColumn("Field2"),
        PersistentColumn("Field3"),
        BasicColumn("Field4")
      )
    )
  }

  "WriterColumnExpressionGenerator" should "parse 'non-empty' column types" in {
    WriterColumnExpressionGenerator.generateExpressionList(
      """Field1 NonEmpty
        |Field2 nonempty
        |# This is a comment
        |Field3 Persistent
        |## This is also a comment
        |Field4 Non-Empty
      """.stripMargin) should equal (
      Array(
        NonEmptyColumn("Field1"),
        NonEmptyColumn("Field2"),
        PersistentColumn("Field3"),
        NonEmptyColumn("Field4")
      )
    )
  }

  "WriterColumnExpressionGenerator" should "treat pipes as terminating characters" in {
    WriterColumnExpressionGenerator.generateExpressionList(
      """
Field1 NonEmpty | Field2 nonempty | # This is a comment
Field3 Persistent
## This is also a comment
Field4 Non-Empty
      """) should equal (
      Array(
        NonEmptyColumn("Field1"),
        NonEmptyColumn("Field2"),
        PersistentColumn("Field3"),
        NonEmptyColumn("Field4")
      )
    )
  }

  "WriterColumnExpressionGenerator" should "ignore empty lines" in {
    WriterColumnExpressionGenerator.generateExpressionList(
      """Field1 NonEmpty
        |Field2 nonempty
        |
        |# This is a comment
        |Field3 Persistent
        |
        |
        |
        |## This is also a comment
        |Field4 Non-Empty
        |
      """.stripMargin) should equal (
      Array(
        NonEmptyColumn("Field1"),
        NonEmptyColumn("Field2"),
        PersistentColumn("Field3"),
        NonEmptyColumn("Field4")
      )
    )
  }

  "WriterColumnExpressionGenerator" should "throw WriterColumnException on a bad line" in {
    val thrown = intercept[WriterColumnException]{WriterColumnExpressionGenerator.generateExpressionList("^cat")}
    thrown.getMessage should equal ("Unknown WriterColumn Type: '^cat'")
  }
}
