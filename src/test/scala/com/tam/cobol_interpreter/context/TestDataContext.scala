package com.tam.cobol_interpreter.test.context

import com.tam.cobol_interpreter.context.{ContextTool, DataContext}
import org.junit.runner.RunWith
import org.scalatest.FlatSpec
import org.scalatest.Matchers._
import org.scalatest.junit.JUnitRunner

/**
 * Created by tamu on 1/2/15.
 */
@RunWith(classOf[JUnitRunner])
class TestDataContext extends FlatSpec {

  "DataContext" should "keep string values in arrays" in {
    val dc = new DataContext()
    dc.add("Test1", "val1")
    dc.add("Test2", "val2")
    dc.add("Test1", "val3".toCharArray.map(_.toByte).toArray)
    dc("Test1") should equal (Array("val1".toCharArray, "val3".toCharArray))
    dc("Test2") should equal (Array("val2".toCharArray))
  }

  "DataContext" should "get data should return empty stuff" in {
    val dc = new DataContext()
    dc.getData("hi") should equal (ContextTool.defaultData())
  }
}
