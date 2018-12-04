package com.cory.playground.test

import com.cory.playground.Cats._

class CatsSpec extends BaseSpec {
  "mergeMaps" should {
    "return the non-empty map when the second map is empty" in {
      val map1 = Map(
        "a" -> 1,
        "b" -> 2
      )

      val result = mergeMaps(map1, Map[String, Int]())

      result should equal (map1)
    }

    "return the non-empty map when the first map is empty" in {
      val map2 = Map(
        "a" -> 1,
        "b" -> 2
      )

      val result = mergeMaps(Map[String, Int](), map2)

      result should equal (map2)
    }

    "overwrite values from the first map when found in the second map" in {
      val map1 = Map(
        "a" -> 1,
        "b" -> 2
      )
      val map2 = map1.updated("a", 3)

      val result = mergeMaps(Map[String, Int](), map2)

      result should equal (Map(
        "a" -> 3,
        "b" -> 2
      ))
    }

    "leave values from the first map when not found in the second map" in {
      val map1 = Map(
        "a" -> 1,
        "b" -> 2
      )
      val map2 = Map(
        "b" -> 3
      )

      val result = mergeMaps(map1, map2)

      result should equal (Map(
        "a" -> 1,
        "b" -> 3
      ))
    }

  }
}
