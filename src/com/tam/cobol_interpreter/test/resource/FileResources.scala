package com.tam.cobol_interpreter.test.resource

import java.io.FileInputStream

/**
 * Created by tamu on 1/6/15.
 */
object FileResources {
  object cobol {
    def test1 = () => new FileInputStream("/Users/tamu/Projects/scala-cobol-interpreter/src/com/tam/cobol_interpreter/test/resource/cobol/cobolTest1.cobol")
    def test2 = () => new FileInputStream("/Users/tamu/Projects/scala-cobol-interpreter/src/com/tam/cobol_interpreter/test/resource/cobol/cobolTest2.cobol")
  }
  object parserSchemas {
    def test1 = () => new FileInputStream("/Users/tamu/Projects/scala-cobol-interpreter/src/com/tam/cobol_interpreter/test/resource/schemas/parserTest1.parser")
    def test2 = () => new FileInputStream("/Users/tamu/Projects/scala-cobol-interpreter/src/com/tam/cobol_interpreter/test/resource/schemas/parserTest2.parser")
  }
  object writerSchemas {
    def test1 = () => new FileInputStream("/Users/tamu/Projects/scala-cobol-interpreter/src/com/tam/cobol_interpreter/test/resource/schemas/writerTest1.writer")
    def test2 = () => new FileInputStream("/Users/tamu/Projects/scala-cobol-interpreter/src/com/tam/cobol_interpreter/test/resource/schemas/writerTest2.writer")
    def test3 = () => new FileInputStream("/Users/tamu/Projects/scala-cobol-interpreter/src/com/tam/cobol_interpreter/test/resource/schemas/writerTest3.writer")
  }
}
