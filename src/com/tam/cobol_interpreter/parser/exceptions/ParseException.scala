package com.tam.cobol_interpreter.parser.exceptions

import com.tam.cobol_interpreter.tools.ByteArrayTool

/**
 * Created by tamu on 1/6/15.
 */
class ParseException(s:String) extends Exception(s)
class ParserException(s:String, nodeName:String, badBytePosition:Int, readBytes:Array[Byte]) extends ParseException(
  s"Node:'$nodeName' Position:$badBytePosition Bytes:'${ByteArrayTool.makeString(readBytes)}' Msg:'$s'"
){
  def getBadBytePosition = this.badBytePosition
  def getReadBytes = this.readBytes
}
class ParseBranchException(s:String, nodeName:String, pointerPosition:Int) extends ParseException(s)
class ParseNodeException(s:String, nodeName:String, pointerPosition:Int, readBytes:Array[Byte]) extends ParseException(s) {
  def getNodeName:String = this.nodeName
  def getPointerPosition = this.pointerPosition
  def getReadBytes = this.readBytes
  def getBadBytePosition = this.getPointerPosition - this.getReadBytes.length
}
class ParseSwitchValueException(s:String, nodeName:String, pointerPosition:Int, readBytes:Array[Byte]) extends ParseNodeException(s, nodeName, pointerPosition, readBytes)
