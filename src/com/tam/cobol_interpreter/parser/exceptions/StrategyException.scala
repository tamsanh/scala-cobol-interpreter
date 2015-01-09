package com.tam.cobol_interpreter.parser.exceptions

/**
 * Created by tamu on 1/6/15.
 */
class StrategyException(s:String, readBytes:Array[Byte]) extends Exception(s) {
  def getReadBytes:Array[Byte] = this.readBytes
}
class IntStrategyException(s:String, readBytes:Array[Byte]) extends
  StrategyException(s, readBytes)
