package com.tam.cobol_interpreter.parser.exceptions

/**
 * Created by tamu on 1/4/15.
 */
class SchemaException(s:String) extends Exception(s){
  def this() = this("")
}
//TODO: Possibly reduce this duplication
case class SwitchCaseException(s:String, pointerPosition:Int, readBytes:Array[Byte], nodeName:String) extends SchemaException(s)
