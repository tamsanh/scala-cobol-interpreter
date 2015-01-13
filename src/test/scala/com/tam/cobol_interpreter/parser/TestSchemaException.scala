package com.tam.cobol_interpreter.test.parser

import com.tam.cobol_interpreter.parser.exceptions.SchemaException
import org.junit.runner.RunWith
import org.scalatest.FlatSpec
import org.scalatest.Matchers._
import org.scalatest.junit.JUnitRunner

/**
 * Created by tamu on 1/4/15.
 */
@RunWith(classOf[JUnitRunner])
class TestSchemaException extends FlatSpec{
  "SchemaException" should "give itself a blank message" in {
    new SchemaException().getMessage should equal ("")
  }
}
