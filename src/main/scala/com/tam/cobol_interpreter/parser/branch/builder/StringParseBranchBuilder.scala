package com.tam.cobol_interpreter.parser.branch.builder

import com.tam.cobol_interpreter.context.ParseContext
import com.tam.cobol_interpreter.parser.branch.ParseBranch

/**
 * Created by tamu on 1/3/15.
 */
class StringParseBranchBuilder extends ParseBranchBuilder{
  var branchString = new StringBuffer()
  var length:Int = 0

  def getCurrentLength:Int = this.length

  private def append(s: String, b:Int): Unit = {
    this.branchString.append(s + "\n")
  }

  override def initializeParseBranch(): Unit = {
    this.branchString = new StringBuffer()
    append("Initialized", 0)
  }

  override def setParseContext(pc: ParseContext): Unit = append(s"ParseContext: ${pc.length}", 0)

  private def appendNode(name:String, bytes: Int, nType:String): Unit = {
    this.length += bytes
    append(s"Node: $nType '$name' $bytes", bytes)
  }

  override def addComp3Node(name: String, bytes: Int): Unit = appendNode(name, bytes, "Comp3")

  override def addCharNode(name: String, bytes: Int): Unit = appendNode(name, bytes, "Char")

  override def addIntNode(name: String, bytes: Int): Unit = appendNode(name, bytes, "Int")

  override def addFillNode(bytes: Int): Unit = appendNode("Filler", bytes, "Fill")

  override def getParseBranch: ParseBranch = {
    this.branchString.append("Built")
    new ParseBranch()
  }

  override def addNode(name:String, bytes: Int, strategy:String): Unit = appendNode(name, bytes, strategy.capitalize)

  def getStringParseBranch: String = this.branchString.toString
}
