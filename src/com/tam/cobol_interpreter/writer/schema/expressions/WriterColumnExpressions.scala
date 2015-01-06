package com.tam.cobol_interpreter.writer.schema.expressions

/**
 * Created by tamu on 1/5/15.
 */
abstract class WriterColumnExpression(columnName:String) {
  def getColumnName: String = this.columnName
}
// If this column is set, set the WriteContext reset flag
case class PersistentColumn(columnName:String) extends WriterColumnExpression(columnName)
case class BasicColumn(columnName:String) extends WriterColumnExpression(columnName)
case class NonEmptyColumn(columnName:String) extends WriterColumnExpression(columnName)
