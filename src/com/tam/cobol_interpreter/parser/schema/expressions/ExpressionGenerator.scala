package com.tam.cobol_interpreter.parser.schema.expressions

import com.tam.cobol_interpreter.parser.exceptions.SchemaException
import com.tam.cobol_interpreter.parser.schema.expressions.ExpressionMatcher._

/**
 * Created by tamu on 1/4/15.
 */
object ExpressionGenerator {
    def recGenerateCases(schemaLines: Array[String]): (Array[Case], Array[String]) = {
      if (schemaLines.length <= 0)
        (new Array[Case](0), schemaLines)
      else
        schemaLines.head match {
          case CaseMatcher(switchVal) =>
            val (caseExpressions, schemaLinesCutCase) = recGenerateExpressionTree(schemaLines.tail)
            val (caseList, schemaLinesCut) = recGenerateCases(schemaLinesCutCase)
            (new Case(switchVal.toCharArray.map(_.toByte).toArray, caseExpressions) +: caseList, schemaLinesCut)
          case CommentMatcher() =>
            recGenerateCases(schemaLines.tail)
          case EmptySpaceMatcher() =>
            recGenerateCases(schemaLines.tail)
          case _ =>
            (new Array[Case](0), schemaLines)
        }
    }

    def recGenerateExpressionTree(schemaLines: Array[String]): (Array[ParserSchemaExpression], Array[String]) = {
      if (schemaLines.length <= 0)
        (new Array[ParserSchemaExpression](0), schemaLines)
      else
        schemaLines.head match {
          case ColumnMatcher(name, typ, bytes) =>
            val (tree, schemaLinesCut) = recGenerateExpressionTree(schemaLines.tail)
            (new Column(name, typ, bytes) +: tree, schemaLinesCut)
          case FillerMatcher(bytes) =>
            val (tree, schemaLinesCut) = recGenerateExpressionTree(schemaLines.tail)
            (new Filler(bytes) +: tree, schemaLinesCut)
          case SwitchMatcher(name, typ, bytes) =>
            val (cases: Array[Case], schemaLinesCaseCut: Array[String]) = recGenerateCases(schemaLines.tail)
            val (tree, schemaLinesCut) = recGenerateExpressionTree(schemaLinesCaseCut)
            (new Switch(new Column(name, typ, bytes), cases) +: tree, schemaLinesCut)
          case OccursMatcher(bytes) =>
            val (occursExpressions: Array[ParserSchemaExpression], schemaLinesOccursCut: Array[String]) = recGenerateExpressionTree(schemaLines.tail)
            val (tree, schemaLinesCut) = recGenerateExpressionTree(schemaLinesOccursCut)
            (new Occurs(bytes, occursExpressions) +: tree, schemaLinesCut)
          case EndOccursMatcher() =>
            (new Array[ParserSchemaExpression](0), schemaLines.tail)
          case EndCaseMatcher() =>
            (new Array[ParserSchemaExpression](0), schemaLines.tail)
          case CommentMatcher() =>
            recGenerateExpressionTree(schemaLines.tail)
          case EmptySpaceMatcher() =>
            recGenerateExpressionTree(schemaLines.tail)
          case x =>
            throw new SchemaException(s"Unrecognized ParserSchemaExpression: '$x'")
        }
    }

    def getTableName(schemaLines:Array[String]): (Option[TableName], Array[String]) = {
      if(schemaLines.length <= 0)
        (None, schemaLines)
      else
        schemaLines.head match {
          case CommentMatcher() =>
            getTableName(schemaLines.tail)
          case EmptySpaceMatcher() =>
            getTableName(schemaLines.tail)
          case TableNameMatcher(x) =>
            (Some(TableName(x)), schemaLines.tail)
          case _ =>
            (None, schemaLines)
        }
    }

    def generateExpressionTree(schemaString: String): Array[ParserSchemaExpression] = {
      //TODO: Consolidate this duplicated function
      val schemaLines = schemaString.trim().
        split('\n').
        filter({case CommentMatcher() => false case _ => true}).mkString("\n").
        split('|').
        filter({case CommentMatcher() => false case _ => true}).mkString("|").
        split(Array('|','\n'))

      val (tableName, schemaLinesTableCut) = getTableName(schemaLines)

      val rowBytes = schemaLinesTableCut.last match {
        case RowBytesMatcher(b) => new RowBytes(b)
        case _ => throw new Exception("Row Bytes Unspecified. Must be last.")
      }

      val (tree, _) = recGenerateExpressionTree(schemaLinesTableCut.dropRight(1))
      tableName match {
        case Some(t) => t +: tree :+ rowBytes
        case None => tree :+ rowBytes
      }
    }
}
