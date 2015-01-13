package com.tam.cobol_interpreter.tools

/**
 * Created by tamu on 1/1/15.
 */

object Comp3Tool {

  def unpack(data: Array[Byte]): Array[Byte] = {
    val comp_digits = data.dropRight(1)
    val digits = comp_digits.flatMap({ k =>
      val left = (k & 0xF0) >> 4
      val right = k & 0x0F
      List(left.toInt, right.toInt)
    }).mkString
    val last_digit = (data.last & 0xF0) >> 4
    val comp_sign = data.last & 0x0F
    (if (comp_sign == 0x0B || comp_sign == 0x0D)
      "-" + (digits.mkString + last_digit.toString)
    else
      digits.mkString + last_digit.toString
    ).toInt.toString.toCharArray.map(_.toByte).toArray
  }

  def pack(data: String): Array[Byte] = this.pack(data.toInt)

  def pack(data: Int): Array[Byte] = {
    val isNegative = data < 0

    // Filter leading 0s, negative sign
    val dataArr =
      if(isNegative)
        data.toString.toCharArray.drop(1)
      else
        data.toString.toCharArray

    var retByte = 0
    for(b <- dataArr) {
      retByte = retByte | b.toString.toInt.toByte
      retByte = retByte << 4
    }
    retByte = retByte | (if (isNegative) { 0x0D } else { 0x0C })

    val byteCount = Math.ceil(0.05 + (dataArr.length.toDouble / 2:Double)).toInt

    val retArr = new Array[Byte](byteCount)
    for(i <- 0 until byteCount) {
      retArr(i) = (retByte >> (i * 8) & 0xFF).toByte
    }
    retArr.reverse
  }

  def pack(data:Int, size:Int):Array[Byte] = {
    val packed = pack(data)
    assert(size >= packed.length, s"Passed Size ($size) Smaller than Target Size (${packed.length})")
    val ret = new Array[Byte](size)
    var i = size - 1
    for(b <- packed.reverse) {
      ret(i) = b
      i -= 1
    }
    while(i >= 0){
      ret(i) = 0x00
      i -= 1
    }
    ret
  }
}
