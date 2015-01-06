package com.tam.cobol_interpreter.examples.StoreDeliveryTable

import java.io.FileOutputStream
import java.util.Random

import com.tam.cobol_interpreter.tools.{ByteArrayTool, Comp3Tool}

/**
 * Created by tamu on 1/6/15.
 */
object GenerateStoreDeliveryTable {
  def main(arg:Array[String]) {
    val fos = new FileOutputStream("./src/com/tam/cobol_interpreter/examples/StoreDeliveryTable/Store_Delivery_Table.cobol")
    val names = Array("Sophie's Free \"Gym\"",
                      "Butters' Mochi Shop",
                      "Div n' Jared's Juice",
                      "Gas To The Max",
                      "James' Japanese Tea").
      map({ k =>
        var j = k
        while(j.length < 25) j += " "
        j
      })
    val rand = new Random()
    for (s <- 1 to names.length) {
      fos.write(ByteArrayTool.stringToByteArray(s"T000$s${names(s - 1)}"))
      for (d <- 1 to 4) {
        fos.write(ByteArrayTool.stringToByteArray("R2014010" + d))
        for (i <- 1 to 3) {
          fos.write(Comp3Tool.pack(100 + s+d+i + Math.abs(rand.nextInt() % 1000), 3))
          fos.write(Comp3Tool.pack(10 + s + d + i, 3))
          fos.write(ByteArrayTool.stringToByteArray(i.toString))
        }
      }
    }
  }
}
