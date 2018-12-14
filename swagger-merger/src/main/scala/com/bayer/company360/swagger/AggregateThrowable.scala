package com.bayer.company360.swagger

import com.bayer.company360.swagger.StringExtender._

class AggregateThrowable(exceptions: Traversable[Throwable]) extends Throwable {
  override def getMessage: String = {
    val innerExceptionMessages = exceptions.toList
      .zipWithIndex
      .map {
        case (throwable, index) => s"Error ${index+1}:\n${throwable.getMessage.tabIndentLines}"
      }

    s"An aggregate exception occurred:${newLine}${innerExceptionMessages.mkString(newLine)}"
  }
}
