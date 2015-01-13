package com.tam.cobol_interpreter.parser.exceptions

/**
 * Created by tamu on 1/4/15.
 */
class SchemaException(s:String) extends Exception(s){
  def this() = this("")
}
class SwitchCaseException(s:String) extends SchemaException(s)
class MultipleCaseValues(caseValue:String) extends SwitchCaseException(s"Multiple Case Values of: '$caseValue'")
class MultipleDefaultCases(defaultValue:String) extends SwitchCaseException(s"Multiple Default Cases. Default Value: '$defaultValue'")
