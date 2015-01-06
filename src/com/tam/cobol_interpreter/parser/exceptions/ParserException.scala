package com.tam.cobol_interpreter.parser.exceptions

/**
 * Created by tamu on 1/6/15.
 */
class ParserException(s:String) extends Exception(s)
case class ParseNodeException(s:String, nodeName:String, pointerPosition:Int, readBytes:Array[Byte]) extends ParserException(s) {
  def getNodeName:String = this.nodeName
}
