package com.bayer.company360.swagger.test

trait WrapsInOption {
  implicit def wrapInOption[U, T <: U](value: T): Option[U] = {
    Option(value)
  }
}
