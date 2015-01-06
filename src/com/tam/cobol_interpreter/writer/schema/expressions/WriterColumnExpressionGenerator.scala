package com.tam.cobol_interpreter.writer.schema.expressions

import WriterColumnExpressionMatcher._
import com.tam.cobol_interpreter.writer.schema.exceptions.WriterColumnException

/**
 * Created by tamu on 1/5/15.
 */
object WriterColumnExpressionGenerator {
  def recGenerateExpressionList(schemaLines: Array[String]): Array[WriterColumnExpression] = {
    if(schemaLines.length <= 0)
      new Array[WriterColumnExpression](0)
    else
      schemaLines.head match {
        case BasicColumnMatcher(x) =>
          BasicColumn(x) +: recGenerateExpressionList(schemaLines.tail)
        case PersistentColumnMatcher(x) =>
          PersistentColumn(x) +: recGenerateExpressionList(schemaLines.tail)
        case CommentMatcher() =>
          recGenerateExpressionList(schemaLines.tail)
        case EmptySpaceMatcher() =>
          recGenerateExpressionList(schemaLines.tail)
        case NonEmptyColumnMatcher(x) =>
          NonEmptyColumn(x) +: recGenerateExpressionList(schemaLines.tail)
        case x =>
          throw new WriterColumnException(s"Unknown WriterColumn Type: '$x'")
      }
  }
  def generateExpressionList(schemaString:String): Array[WriterColumnExpression] = {
    //TODO: Consolidate this duplicated function
    val schemaLines = schemaString.trim().
      split('\n').
      filter({case CommentMatcher() => false case _ => true}).mkString("\n").
      split('|').
      filter({case CommentMatcher() => false case _ => true}).mkString("|").
      split(Array('|','\n'))

    recGenerateExpressionList(schemaLines)
  }
}
