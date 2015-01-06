package com.tam.cobol_interpreter.parser.branch

import com.tam.cobol_interpreter.context.{DataContext, ParseContext}
import com.tam.cobol_interpreter.parser.exceptions.{ParseNodeException, StrategyException, IntStrategyException}
import com.tam.cobol_interpreter.parser.strategy.ParseStrategy

import scala.collection.mutable

/**
 * Created by tamu on 1/1/15.
 */

class ParseBranch(var parseContext: Option[ParseContext]) {
  /**
   * The parsing mechanism which parses a ParseContext
   * ParseBranches are tailored to fit a given ParseContext
   */
  val parsedData = new DataContext
  var nodeList = new mutable.LinkedList[ParseNode]()
  var nodeByteSize = 0

  def this() = this(None: Option[ParseContext])
  def this(parseContext: ParseContext) = this(Some(parseContext))

  def getParseContext: ParseContext = this.parseContext match {
    case Some(x) => x
    case _ => throw new Exception("No ParseContext Set")
  }

  def setParseContext(pc: ParseContext): Unit = {
    this.parseContext = Some(pc)
  }

  def setData(nodeName:String, nodeData:Array[Byte]): Unit = {
    parsedData.add(nodeName, nodeData)
  }

  def addNode(name: String, bytes: Int, parseStrategy: ParseStrategy): Unit ={
    nodeList = nodeList :+ new ParseNode(this, name, bytes, parseStrategy)
    nodeByteSize += bytes
  }

  def parse(): DataContext = {
    val assumeMessage = s"Total nodes' bytes larger than ParseContext: ${this.nodeByteSize} > ${this.getParseContext.length}"
    assume(this.nodeByteSize <= this.getParseContext.length, assumeMessage)
    for(node <- nodeList)
      try
        node.parse()
      catch {
        case e:StrategyException =>
          throw new ParseNodeException(e.getMessage, node.getName, e.getPointerPosition, e.getReadBytes)
      }
    this.parsedData
  }
}
