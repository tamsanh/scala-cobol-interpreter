package com.tam.cobol_interpreter.examples.StoreDeliveryTable

import java.io.{FileInputStream, FileOutputStream}

import com.tam.cobol_interpreter.InterpreterFactory

/**
 * Created by tamu on 1/6/15.
 */
object InterpretStoreDeliveryTable {
  def main(args:Array[String]):Unit = {
    GenerateStoreDeliveryTable.main(args)

    val i = InterpreterFactory.createInterpreter(
      new FileInputStream("./src/com/tam/cobol_interpreter/examples/StoreDeliveryTable/Store_Delivery_Table.parser"),
      new FileInputStream("./src/com/tam/cobol_interpreter/examples/StoreDeliveryTable/Store_Delivery_Table.writer")
    )
    i.setWriteHeader(true).interpret(
      new FileInputStream("./src/com/tam/cobol_interpreter/examples/StoreDeliveryTable/Store_Delivery_Table.cobol"),
      new FileOutputStream("./src/com/tam/cobol_interpreter/examples/StoreDeliveryTable/Store_Delivery_Table.output")
    )
  }
}
