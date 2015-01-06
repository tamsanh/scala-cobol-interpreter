package com.tam.cobol_interpreter.test.parser

import com.tam.cobol_interpreter.parser.schema.expressions.ExpressionMatcher._
import org.scalatest.FlatSpec
import org.scalatest.Matchers._


/**
 * Created by tamu on 1/3/15.
 */
class TestSchemaMatcher extends FlatSpec {
  "TableNameMatcher" should "match table names" in {
    ("Table_Name" match {
      case TableNameMatcher(t) => t
    }) should equal ("Table_Name")
  }

  "CommentMatcher" should "match comments" in {
    ("# This is a comment" match {
      case CommentMatcher() => "Good"
    }) should equal ("Good")
    ("### This is a comment" match {
      case CommentMatcher() => "Good"
    }) should equal ("Good")
    ("  # Another" match {
      case CommentMatcher() => "Good"
    }) should equal ("Good")
  }

  "ColumnMatcher" should "match columns" in {
    ("n t b" match {
      case ColumnMatcher(n, t, b) => "Bad"
      case _ => "Good"
    }) should equal ("Good")

    ("n t 3" match {
      case ColumnMatcher(n, t, b) =>
        Array(n, t, b)
    }) should equal (Array("n", "t", "3"))
  }

  "OccursMatcher" should "match occurs" in {
    ("Occurs 3" match {
      case OccursMatcher(b) =>
        b
    }) should equal ("3")

    ("Occurs x" match {
      case OccursMatcher(b) => "Bad"
      case _ => "Good"
    }) should equal ("Good")
  }

  "SwitchMatcher" should "match a switch string" in {
    ("switch n t 3" match {
      case SwitchMatcher(n, t, b) => Array(n, t, b)
    }) should equal (Array("n", "t", "3"))
    ("switch n t b" match {
      case SwitchMatcher(n, t, b) => "Bad Match"
      case _=> "Good"
    }) should equal ("Good")
  }

  "CaseMatcher" should "match a case string" in {
    ("case x" match {
      case CaseMatcher(x) =>
        x
    }) should equal ("x")
    ("case -1" match {
      case CaseMatcher(x) =>
        x
    }) should equal ("-1")
  }

  "EndMatcher" should "match terminating expressions" in {
    ("endOccurs" match {
      case EndOccursMatcher() => "Good"
    }) should equal ("Good")

    ("endCase" match {
      case EndCaseMatcher() => "Good"
    }) should equal ("Good")
  }

  "FillerMatcher" should "match fillers" in {
    ("filler 3" match {
      case FillerMatcher(b) => b
    }) should equal ("3")

    ("filler a" match {
      case FillerMatcher(b) => b
      case _ => "Good"
    }) should equal ("Good")
  }

  "RowByteMatcher" should "match lonely integers" in {
    ("123" match {
      case RowBytesMatcher(rbm) => rbm
    }) should equal ("123")
    ("abc" match {
      case RowBytesMatcher(rmb) => rmb
      case _ => "Good"
    }) should equal ("Good")
  }

}
