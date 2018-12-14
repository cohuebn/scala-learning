package com.bayer.company360.swagger

object StringExtender {
  val newLine = sys.props("line.separator")

  implicit class StringExtensions(value: String) {
    def tabIndentLines: String = {
      value.split("\r?\n")
        .map(line => s"\t$line")
        .mkString(newLine)
    }
  }
}
