package com.tam.cobol_interpreter.parser.strategy

import com.tam.cobol_interpreter.context.ParseContext
import com.tam.cobol_interpreter.parser.exceptions.{StrategyException, IntStrategyException}
import com.tam.cobol_interpreter.tools.Comp3Tool

/**
 * Created by tamu on 1/1/15.
 */


// TODO: Consider using singleton objects
abstract class ParseStrategy {
  def parse(byteArr: Array[Byte], bytes: Int): Array[Byte] = parse(new ParseContext(byteArr), bytes)
  def getNextBytes(parseContext: ParseContext, bytes:Int):Array[Byte] =
    try
      parseContext.getNextBytes(bytes)
    catch {
      case e:AssertionError =>
        throw new StrategyException(s"Pointer Out of Bounds", parseContext.pointer, parseContext.getData)
      case e:Exception =>
        throw e
    }
  def parse(parseContext: ParseContext, bytes: Int): Array[Byte]
}

class IntStrategy extends ParseStrategy {
  def parse(parseContext: ParseContext, bytes: Int): Array[Byte] = {
    val toParse = getNextBytes(parseContext, bytes)
    try
      toParse.map(_.toChar).mkString.toInt.toString.toCharArray.map(_.toByte).toArray
    catch {
      case e:Exception =>
        throw new IntStrategyException(s"IntStrategyException: ${e.getMessage}", parseContext.pointer, toParse)
    }
  }
}

class CharStrategy extends ParseStrategy {
  def parse(parseContext: ParseContext, bytes: Int): Array[Byte] = {
    getNextBytes(parseContext, bytes)
  }
}

class Comp3Strategy extends ParseStrategy {
  def parse(parseContext: ParseContext, bytes: Int): Array[Byte] = {
    val toParse = getNextBytes(parseContext, bytes)
    Comp3Tool.unpack(toParse)
  }
}

class FillStrategy extends ParseStrategy {
  def parse(parseContext: ParseContext, bytes: Int): Array[Byte] = {
    parseContext.getNextBytes(bytes)
    new Array[Byte](0)
  }
}
