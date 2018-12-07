package com.bayer.company360.swagger.test

trait WrapsInOption {
  implicit def wrapInOption[T](value: T): Option[T] = {
    Option(value)
  }
}
