package com.tam.cobol_interpreter.parser.strategy

/**
 * Created by tamu on 1/4/15.
 */


object ParseStrategyFactory {
  val IntStrategyName = "int"
  val CharStrategyName = "char"
  val Comp3StrategyName = "comp3"
  val FillStrategyName = "fill"

  def getStrategy(s:String): ParseStrategy = {
    s.toLowerCase match {
      case IntStrategyName => IntStrategy
      case CharStrategyName => CharStrategy
      case Comp3StrategyName => Comp3Strategy
      case FillStrategyName => FillStrategy
    }
  }
}
