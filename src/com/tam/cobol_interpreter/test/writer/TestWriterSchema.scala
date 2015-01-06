package com.tam.cobol_interpreter.test.writer

import com.tam.cobol_interpreter.context.{DataContext, WriteContext}
import com.tam.cobol_interpreter.tools.ByteArrayTool
import com.tam.cobol_interpreter.writer.schema.WriterSchemaFactory
import org.scalatest.FlatSpec
import org.scalatest.Matchers._

/**
 * Created by tamu on 1/5/15.
 */
class TestWriterSchema extends FlatSpec{
  "WriterSchema" should "properly modify a WriteContext" in {
    val wc = new WriteContext()
    val ws = WriterSchemaFactory.createSchema(
      """Field1
        |Field2
      """.stripMargin)
    val dc = new DataContext()
    dc.add("Field1", "one")
    dc.add("Field2", "two")
    ws.updateWriteContext(dc, wc)
    wc.getOrderedColumnData should equal (
      Array(
        Array(
          ByteArrayTool.stringToByteArray("one")
        ),
        Array(
          ByteArrayTool.stringToByteArray("two")
        )
      )
    )
  }

  "WriterSchema" should "make a WriteContext un-writable if reset" in {
    val wc = new WriteContext()
    val ws = WriterSchemaFactory.createSchema(
    """Field1 persist
      |Field2
    """.stripMargin)
    val dc = new DataContext()
    dc.add("Field1", "0")
    ws.updateWriteContext(dc, wc)
  }

  "WriterSchema" should "not write if there is a empty non-empty field" in {
    val wc = new WriteContext()
    val ws = WriterSchemaFactory.createSchema(
      """Field1 persist
        |Field2 nonempty
      """.stripMargin)
    val dc = new DataContext()
    dc.add("Field1", "0")
    assert(wc.isWritable)
    ws.updateWriteContext(dc, wc)
    assert(!wc.isWritable)
  }
}
