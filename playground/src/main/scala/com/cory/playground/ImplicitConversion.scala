package com.cory.playground

object BooleanConverter {
  implicit def toBoolean(value: String): Boolean = {
    if (value.equalsIgnoreCase("yes"))
      return true
    if (value.equalsIgnoreCase("no"))
      return false

    throw new IllegalArgumentException("Not convertable to a boolean")
  }
}