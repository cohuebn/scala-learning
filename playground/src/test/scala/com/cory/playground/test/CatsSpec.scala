package com.cory.playground.test

import cats.Semigroup
import cats.implicits._
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

    "what does a semigroup do with functions" in {
      val funcSemigroup = Semigroup[Int => Int].combine(_ + 1, _ * 10)
      val result = funcSemigroup.apply(6)

      result should equal(67)
    }

    "what does a semigroup do with maps" in {
      val semi = Map("foo" -> Map("bar" -> 5)).combine(Map("foo" -> Map("bar" -> 6), "baz" -> Map()))
      semi should equal(Map("foo" -> Map("bar" -> 11), "baz" -> Map()))

      implicit val mapSeqSemigroup: Semigroup[Map[String, Seq[Int]]] = (x: Map[String, Seq[Int]], y: Map[String, Seq[Int]]) => {
        y.foldLeft(x) {
          case (agg, (key, value)) =>
            val updated = x.getOrElse(key, Seq()) ++ value
            agg.updated(key, updated)
        }
      }

      val combinedSeqMaps = Map("path1" -> Seq(1), "path2" -> Seq(2)) |+| Map("path1" -> Seq(3))
      combinedSeqMaps("path1") should equal(Seq(1, 3))
      combinedSeqMaps("path2") should equal(Seq(2))
    }

    "what does a semigroup do with a map of lists" in {
      val combinedIntSeqMaps = Map("path1" -> List(1), "path2" -> List(2)) |+| Map("path1" -> List(3, 4))
      combinedIntSeqMaps("path1") should equal(Seq(1, 3, 4))
      combinedIntSeqMaps("path2") should equal(Seq(2))

      val combinedStringSeqMaps = Map("path1" -> List("yay"), "path2" -> List("wee")) |+| Map("path1" -> List("woopie"))
      combinedStringSeqMaps("path1") should equal(Seq("yay", "woopie"))
      combinedStringSeqMaps("path2") should equal(Seq("wee"))
    }
  }
}
