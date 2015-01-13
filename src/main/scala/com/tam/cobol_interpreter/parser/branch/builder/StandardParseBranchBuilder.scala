package com.tam.cobol_interpreter.parser.branch.builder

import com.tam.cobol_interpreter.context.ParseContext
import com.tam.cobol_interpreter.parser.branch.ParseBranch
import com.tam.cobol_interpreter.parser.strategy._

/**
 * Created by tamu on 1/3/15.
 */
class StandardParseBranchBuilder extends ParseBranchBuilder {
   /**
    * This creates the parse branch used to parse a parse context.
    */

  var branch: ParseBranch = new ParseBranch()
  var length:Int = 0

  def getCurrentLength:Int = this.length

  def initializeParseBranch(): Unit = {
   this.branch = new ParseBranch()
  }

  def setParseContext(pc: ParseContext): Unit = {
   this.branch.setParseContext(pc)
  }

  def addIntNode(name: String, bytes: Int): Unit = addNode(name, bytes, ParseStrategyFactory.IntStrategyName)

  def addCharNode(name: String, bytes: Int): Unit = addNode(name, bytes, ParseStrategyFactory.CharStrategyName)

  def addFillNode(bytes: Int): Unit = addNode("Filler", bytes, ParseStrategyFactory.FillStrategyName)

  def addComp3Node(name: String, bytes: Int): Unit = addNode(name, bytes, ParseStrategyFactory.Comp3StrategyName)

  def addNode(name:String, bytes:Int, strategy:String): Unit = {
    this.length += bytes
    val strategyObj = ParseStrategyFactory.getStrategy(strategy)
    branch.addNode(name, bytes, strategyObj)
  }

  def getParseBranch: ParseBranch = this.branch
}
