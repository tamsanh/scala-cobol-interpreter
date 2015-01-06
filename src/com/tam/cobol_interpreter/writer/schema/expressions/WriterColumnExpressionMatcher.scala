package com.tam.cobol_interpreter.writer.schema.expressions

/**
 * Created by tamu on 1/5/15.
 */

object WriterColumnExpressionMatcher {
  val CommentMatcher = """^\s*#.*$""".r
  val PersistentColumnMatcher = """^\s*(\w+)\s*[pP]ersist[ent]{0,3}\s*$""".r
  val NonEmptyColumnMatcher = """^\s*(\w+)\s*[nN]on[-]?[eE]mpty\s*$""".r
  val BasicColumnMatcher = """^\s*(\w+)\s*$""".r
  val EmptySpaceMatcher = """^\s*$""".r
}
