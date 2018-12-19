package com.cory.playground.test

import com.cory.playground.Shapeless._

class JsonConverterSpec extends BaseSpec {
  def assertOnEncoded[T](input: T, expected: String)(implicit encoder: MyJsonEncoder[T]) = {
    val result = writeJson(input)

    result should equal(expected)
  }

  "A json encoder" should {
    "be able to encode a string" in {
      assertOnEncoded("uno dos tres", "\"uno dos tres\"")
    }

    "be able to encode an array of strings" in {
      assertOnEncoded(Array("uno", "dos", "tres"), "[\"uno\", \"dos\", \"tres\"]")
    }

    "be able to encode a map of strings" in {
      val input = Map(
        "uno" -> "one",
        "dos" -> "two",
        "tres" -> "three",
      )
      val expected = "{ \"uno\": \"one\", \"dos\": \"two\", \"tres\": \"three\" }"

      assertOnEncoded(input, expected)
    }

    "be able to encode an int" in {
      assertOnEncoded(123, "123")
    }

    "be able to encode an array of ints" in {
      assertOnEncoded(Array(1, 2, 3), "[1, 2, 3]")
    }

    "be able to encode a map of ints" in {
      val input = Map(
        "uno" -> 1,
        "dos" -> 2,
        "tres" -> 3,
      )
      val expected = "{ \"uno\": 1, \"dos\": 2, \"tres\": 3 }"

      assertOnEncoded(input, expected)
    }

    "be able to encode a boolean" in {
      assertOnEncoded(true, "true")
    }

    "be able to encode an array of booleans" in {
      assertOnEncoded(Array(true, false, true), "[true, false, true]")
    }

    "be able to encode a map of booleans" in {
      val input = Map(
        "uno" -> true,
        "dos" -> false,
        "tres" -> true,
      )
      val expected = "{ \"uno\": true, \"dos\": false, \"tres\": true }"

      assertOnEncoded(input, expected)
    }

    "be able to encode an arbitrary case class" in {
      case class Arbitrary(name: String, rating: Int, isCool: Boolean)

      val input = Arbitrary("uno", 1, false)
      val expected = "{ \"name\": \"uno\", \"rating\": 1, \"isCool\": false }"

      assertOnEncoded(input, expected)
    }

    "be able to encode an arbitrary array of case classes" in {
      case class Arbitrary(name: String, rating: Int, isCool: Boolean)

      val input = Array(
        Arbitrary("uno", 1, false),
        Arbitrary("dos", 5000, true)
      )
      val expected = "[{ \"name\": \"uno\", \"rating\": 1, \"isCool\": false }, { \"name\": \"dos\", \"rating\": 5000, \"isCool\": true }]"

      assertOnEncoded(input, expected)
    }
  }
}
