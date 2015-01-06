package com.tam.cobol_interpreter.parser.exceptions

/**
 * Created by tamu on 1/6/15.
 */
class StrategyException(s:String, pointerPosition:Int, readBytes:Array[Byte]) extends Exception(s) {
  def getPointerPosition:Int = this.pointerPosition
  def getReadBytes:Array[Byte] = this.readBytes
}
case class IntStrategyException(s:String, pointerPosition:Int, readBytes:Array[Byte]) extends
  StrategyException(s, pointerPosition, readBytes)
