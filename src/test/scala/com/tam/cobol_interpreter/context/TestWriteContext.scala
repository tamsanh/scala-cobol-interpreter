package com.tam.cobol_interpreter.test.context

import com.tam.cobol_interpreter.context.WriteContext
import com.tam.cobol_interpreter.tools.ByteArrayTool
import org.junit.runner.RunWith
import org.scalatest.FlatSpec
import org.scalatest.Matchers._
import org.scalatest.junit.JUnitRunner

/**
 * Created by tamu on 1/5/15.
 */
@RunWith(classOf[JUnitRunner])
class TestWriteContext extends FlatSpec {
  "WriteContext" should "return that a column exists" in {
    val wc = new WriteContext()
    wc.updateColumnData("a", Array())
    assert(wc.columnDataExists("a"))
    assert(!wc.columnDataExists("b"))
    wc.updateColumnData("b", Array())
    assert(wc.columnDataExists("b"))
  }

  "WriteContext" should "properly order its data" in {
    val wc = new WriteContext()
    wc.updateColumnData("a", Array())
    wc.updateColumnData("b", Array())
    wc.updateColumnData("a", Array())
    wc.order.toArray should equal (Array("a", "b"))
  }

  "WriteContext" should "understand when its columnData is different" in {
    val wc = new WriteContext()
    wc.updateColumnData("a", Array())
    assert(wc.isNewColumnData("a",
      Array(ByteArrayTool.stringToByteArray("a"))))
  }

  "WriteContext" should "get relevant data" in {
    val wc = new WriteContext()
    wc.updateColumnData("a", Array(ByteArrayTool.stringToByteArray("cat")))
    wc.updateColumnData("b", Array(ByteArrayTool.stringToByteArray("qwerty")))
    wc.getColumnData("a") should equal (Array(ByteArrayTool.stringToByteArray("cat")))
    wc.getColumnData("b") should equal (Array(ByteArrayTool.stringToByteArray("qwerty")))

    wc.getOrderedColumnData should equal (Array(
     Array(ByteArrayTool.stringToByteArray("cat")),
     Array(ByteArrayTool.stringToByteArray("qwerty"))
    ))
  }
}
