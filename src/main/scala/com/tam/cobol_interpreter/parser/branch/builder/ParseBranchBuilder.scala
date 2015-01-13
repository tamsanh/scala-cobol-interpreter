package com.tam.cobol_interpreter.parser.branch.builder

import com.tam.cobol_interpreter.context.ParseContext
import com.tam.cobol_interpreter.parser.branch.ParseBranch

/**
 * Created by tamu on 1/1/15.
 */

abstract class ParseBranchBuilder {
  /**
   * This creates the parse branch used to parse a parse context.
   */

  def initializeParseBranch(): Unit
  def getCurrentLength:Int
  def setParseContext(pc: ParseContext): Unit 
  def addIntNode(name: String, bytes: Int): Unit 
  def addCharNode(name: String, bytes: Int): Unit 
  def addFillNode(bytes: Int): Unit 
  def addComp3Node(name: String, bytes: Int): Unit
  def addNode(name: String, bytes:Int, strategy:String): Unit
  def getParseBranch: ParseBranch
}

