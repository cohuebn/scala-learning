package com.bayer.company360.swagger.test

class StringExtenderSpec extends Spec {
  import com.bayer.company360.swagger.StringExtender._

  "tabIndentLines" should {
    "put a tab before each line" in {
      val original =
        """I am line 1
          |I am line 2""".stripMargin

      val result = original.tabIndentLines

      val expected = StringContext.treatEscapes(
        """\tI am line 1
          |\tI am line 2""".stripMargin
      )

      result should equal(expected)
    }
  }
}
