package com.bayer.company360.swagger.test

import java.io.File

import org.scalamock.scalatest.MockFactory
import org.scalatest._

trait Spec extends WordSpecLike
  with Matchers
  with MockFactory
  with WrapsInOption
  with EitherValues
  with TryValues {
  def getResourceAsFile(resourceName: String) = {
    new File(getClass.getClassLoader.getResource(resourceName).toURI)
  }

  def assertOnOption[T](option: Option[T])(assertion: T => Unit) = {
    option should not be empty
    assertion(option.get)
  }
}
