package com.cory.playground.test

import com.cory.playground.CirceTranslation._
import io.circe.syntax._
import io.circe.yaml.Printer.FlowStyle
import io.circe.yaml.{Printer, parser}
import org.scalatest.EitherValues

class CirceTranslationSpec extends BaseSpec with EitherValues {
  case class ExampleParent(examples: Examples)

  "an example encoder/decoder" should {
    "encode as expected" in {
      val source =
        """examples:
          |  application/json:
          |    items:
          |    - url: http://cats-fo-days.com/1234
          |      name: Whisker joe
          |      views: 3
          |    limit: 25
          |    offset: 0
        |""".stripMargin

      val decoded = parser.parse(source)
      val encoded = new Printer(preserveOrder = true, indent = 2, dropNullKeys = true, sequenceStyle = FlowStyle.Block).pretty(decoded.right.value.asJson)

      encoded should equal(source.replaceAll("\\r", ""))
    }
  }
}
