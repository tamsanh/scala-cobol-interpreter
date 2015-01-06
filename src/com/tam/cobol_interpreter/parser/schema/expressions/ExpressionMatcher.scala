package com.tam.cobol_interpreter.parser.schema.expressions

import scala.util.matching.Regex

/**
 * Created by tamu on 1/3/15.
 */
object ExpressionMatcher {
  val EmptySpaceMatcher:Regex = """^\s*$""".r
  val CommentMatcher:Regex = """^\s*#.*""".r
  val TableNameMatcher:Regex = """^\s*(\w+)\s*$""".r

  val ColumnMatcher:Regex = """^\s*(\w+)\s+(\w+)\s+(\d+)\s*$""".r

  val OccursMatcher:Regex = """^\s*[oO]ccurs\s+(\d+)\s*$""".r
  val EndOccursMatcher:Regex = """^\s*[eE]nd[oO]ccurs\s*$""".r

  val SwitchMatcher:Regex = """^\s*[sS]witch\s+(\w+)\s+(\w+)\s+(\d+)\s*$""".r
  val CaseMatcher:Regex = """^\s*[cC]ase\s+(.+?)\s*$""".r
  val EndCaseMatcher:Regex = """^\s*[eE]nd[cC]ase\s*$""".r

  val FillerMatcher:Regex = """^\s*[fF]ill[er]{0,2}\s+(\d+)\s*$""".r

  val RowBytesMatcher:Regex = """^\s*(\d+)\s*$""".r
}
