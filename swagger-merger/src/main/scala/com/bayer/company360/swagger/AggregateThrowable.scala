package com.bayer.company360.swagger

class AggregateThrowable(exceptions: Traversable[Throwable]) extends Throwable {
  val newLine = sys.props("line.separator")

  private def tabIndentLines(string: String, tabs: Int = 1) = {
    val indention = "\t" * tabs
    string.split("\r?\n")
      .map(line => s"$indention$line")
      .mkString(newLine)
  }
  override def getMessage: String = {
    val innerExceptionMessages = exceptions.toList
      .zipWithIndex
      .map {
        case (throwable, index) => s"Error ${index+1}:\n${tabIndentLines(throwable.getMessage)}"
      }

    s"An aggregate exception occurred:${newLine}${innerExceptionMessages.mkString(newLine)}"
  }
}
