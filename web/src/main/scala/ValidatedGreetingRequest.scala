package com.cory.web

import com.cory.core.Dialects.dialects

case class ValidatedGreetingRequest(val dialect: String, val name: String) {
  require(dialects contains dialect)
}