package com.tam.cobol_interpreter.parser.branch

import com.tam.cobol_interpreter.parser.exceptions.ParseNodeException
import com.tam.cobol_interpreter.parser.strategy.ParseStrategy

/**
 * Created by tamu on 1/1/15.
 */

class ParseNode(parseBranch: ParseBranch, name: String, bytes: Int, parseStrategy: ParseStrategy) {
  /**
   * Parse of the parse branch, uses a parse strategy to parse a parse context
   */
  def getName:String = this.name
  var data: Array[Byte] = new Array[Byte](0)
  def parse(): Array[Byte] = {
    try
      this.data = this.parseStrategy.parse(this.parseBranch.getParseContext, this.bytes)
    catch {
      case e:ParseNodeException => throw e
    }
    this.parseBranch.setData(this.name, this.data)
    this.data
  }
}
