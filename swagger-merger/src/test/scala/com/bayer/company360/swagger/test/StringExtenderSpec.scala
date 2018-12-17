package com.bayer.company360.swagger.test

class StringExtenderSpec extends Spec {
  import com.bayer.company360.swagger.StringExtender._

  "tabIndentLines" should {
    "put a tab before each line" in {
      val original = s"I am line 1${newLine}I am line 2"

      val result = original.tabIndentLines

      val expected = s"\tI am line 1${newLine}\tI am line 2"

      result should equal(expected)
    }
  }
}
