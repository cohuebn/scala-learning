package com.cory.core

object Dialects {
  val dialectMap = Map(
    "basic" -> "Hi",
    "cowboy" -> "Howdy",
    "butler" -> "Good day",
    "millennial" -> "sup"
  )

  val dialects: Set[String] = dialectMap.keySet
}
