package com.tam.cobol_interpreter.test.writer

import java.io.ByteArrayOutputStream

import com.tam.cobol_interpreter.context.{DataContext, WriteContext}
import com.tam.cobol_interpreter.tools.ByteArrayTool
import com.tam.cobol_interpreter.writer.WriterFactory
import org.scalatest.FlatSpec
import org.scalatest.Matchers._

/**
 * Created by tamu on 1/5/15.
 */
class TestWriter extends FlatSpec {
  "Writer" should "write out DataContexts based on a given schema" in {
    val out = new ByteArrayOutputStream()
    val writer = new WriterFactory().createWriter(
      """
        |Field1
        |Field2
      """.stripMargin, out)
    val dc = new DataContext()
    dc.add("Field1", "a")
    dc.add("Field2", "b")
    val wc = new WriteContext()
    writer.writeWithContext(dc, wc)
    out.toByteArray should equal (ByteArrayTool.stringToByteArray("a|b"))
  }

  "Writer" should "write multiple lines" in {
    val out = new ByteArrayOutputStream()
    val writer = new WriterFactory().createWriter(
      """
        |Field1
        |Field2
      """.stripMargin, out)
    val dc = new DataContext()
    dc.add("Field1", "a")
    dc.add("Field1", "x")
    dc.add("Field2", "b")
    dc.add("Field2", "y")
    val wc = new WriteContext()
    writer.writeWithContext(dc, wc)
    out.toByteArray should equal (ByteArrayTool.stringToByteArray("a|b\nx|y"))
  }

  "Writer" should "keep data persistent" in {
    val out = new ByteArrayOutputStream()
    val wc = new WriteContext()
    val writer = new WriterFactory().createWriter(
      """
        |F1
        |F2 Persist
        |F3
      """.stripMargin, out)
    val dc = new DataContext()
    dc.add("F1", "a")
    dc.add("F2", "b")
    dc.add("F3", "y")
    writer.writeWithContext(dc, wc)
    out.toByteArray should equal (ByteArrayTool.stringToByteArray("a|b|y"))
    val dc2 = new DataContext()
    dc2.add("F3", "z")
    writer.writeWithContext(dc2, wc)
    ByteArrayTool.makeString(out.toByteArray) should equal ("a|b|y\n|b|z")
    val dc3 = new DataContext()
    writer.writeWithContext(dc3, wc)
    out.toByteArray should equal (ByteArrayTool.stringToByteArray("a|b|y\n|b|z\n|b|"))
  }

  "Writer" should "only write if the non-empty field is not empty" in {
    val out = new ByteArrayOutputStream()
    val wc = new WriteContext()
    val writer = new WriterFactory().createWriter(
      """
        |F1 Persistent
        |F2 NonEmpty
        |F3
      """.stripMargin, out)
    val dc = new DataContext()
    dc.add("F1", "a")
    dc.add("F3", "y")
    writer.writeWithContext(dc, wc)
    out.toByteArray should equal (ByteArrayTool.stringToByteArray(""))
    val dc2 = new DataContext()
    dc2.add("F2", "z")
    writer.writeWithContext(dc2, wc)
    out.toByteArray should equal (ByteArrayTool.stringToByteArray("a|z|"))
  }

  "Writer" should "support multiple non-empty fields" in {
    val out = new ByteArrayOutputStream()
    val wc = new WriteContext()
    val writer = new WriterFactory().createWriter(
      """
        |F1 Persistent
        |F2 NonEmpty
        |F3 NonEmpty
      """.stripMargin, out)
    val dc = new DataContext()
    dc.add("F1", "a")
    dc.add("F3", "y")
    writer.writeWithContext(dc, wc)
    out.toByteArray should equal (ByteArrayTool.stringToByteArray(""))
    val dc2 = new DataContext()
    dc2.add("F2", "z")
    writer.writeWithContext(dc2, wc)
    out.toByteArray should equal (ByteArrayTool.stringToByteArray(""))
    val dc3 = new DataContext()
    dc3.add("F2", "z")
    dc3.add("F3", "z")
    writer.writeWithContext(dc3, wc)
    out.toByteArray should equal (ByteArrayTool.stringToByteArray("a|z|z"))
  }

  "Writer" should "write headers" in {
    val out = new ByteArrayOutputStream()
    val wc = new WriteContext()
    val writer = new WriterFactory().createWriter(
      """
        |F1 Persistent
        |F2 NonEmpty
        |F3 NonEmpty
      """.stripMargin, out)
    val dc = new DataContext()
    dc.add("F1", "a")
    dc.add("F2", "z")
    dc.add("F3", "z")
    writer.writeHeaderRow()
    writer.writeWithContext(dc, wc)
    ByteArrayTool.makeString(out.toByteArray) should equal ("F1|F2|F3\na|z|z")
  }

  "Writer" should "properly quote if there is data that needs quoting" in {
    val out = new ByteArrayOutputStream()
    val wc = new WriteContext()
    val writer = new WriterFactory().createWriter(
      """
        |F3
      """.stripMargin, out)
    val dc = new DataContext()
    dc.add("F3", "|what")
    writer.writeWithContext(dc, wc)
    ByteArrayTool.makeString(out.toByteArray) should equal ("\"|what\"")

    val out2 = new ByteArrayOutputStream()
    val wc2 = new WriteContext()
    val writer2 = new WriterFactory().createWriter(
      """
        |F3
      """.stripMargin, out2)
    val dc2 = new DataContext()
    dc2.add("F3", "there's a \" in here")
    writer2.writeWithContext(dc2, wc2)
    ByteArrayTool.makeString(out2.toByteArray) should equal ("\"there's a \\\" in here\"")
  }
}
