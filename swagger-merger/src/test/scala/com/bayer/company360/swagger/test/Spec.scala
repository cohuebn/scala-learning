package com.bayer.company360.swagger.test

import java.io.File

import org.scalamock.scalatest.MockFactory
import org.scalatest.{BeforeAndAfterEach, Matchers, WordSpecLike}

trait Spec extends WordSpecLike
  with Matchers
  with BeforeAndAfterEach
  with MockFactory
  with WrapsInOption {
  def getResourceAsFile(resourceName: String) = {
    new File(getClass.getClassLoader.getResource(resourceName).toURI)
  }
}
