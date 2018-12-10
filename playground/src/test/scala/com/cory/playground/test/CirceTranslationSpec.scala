package com.cory.playground.test

import io.circe.parser.decode
import io.circe.generic.auto._
import com.cory.playground.CirceTranslation._
import org.scalatest.EitherValues

class CirceTranslationSpec extends BaseSpec with EitherValues {
  case class ExampleParent(examples: Examples) // examples: { mime-type: {example} }

  "an example encoder/decoder" should {
    "parse an example into the expected object" in {
      val json =
        """{
          | "examples": {
          |   "application/json": "i work",
          | }
          |}
        """.stripMargin

      val decoded = decode[ExampleParent](json)

      decoded.right.value.examples.keys should contain theSameElementsAs Seq("application/json", "text/plain")
      val jsonExample = decoded.right.value.examples.get("application/json")
      val textExample = decoded.right.value.examples.get("text/plain")
    }
  }
}
