package com.bayer.company360.swagger.test

import java.io.File

import com.bayer.company360.swagger.{SwaggerFileParser, SwaggerMerger}
import org.scalamock.scalatest.MockFactory
import org.scalatest.{BeforeAndAfterEach, WordSpecLike}

class SwaggerMergerSpec extends WordSpecLike
  with BeforeAndAfterEach
  with MockFactory {

  var swaggerFileParser: SwaggerFileParser = _
  var swaggerMerger: SwaggerMerger = _

  override def beforeEach(): Unit = {
    super.beforeEach()
    swaggerFileParser = stub[SwaggerFileParser]
    swaggerMerger = new SwaggerMerger(swaggerFileParser)
  }

  "A swagger merger" should {
    "read all swagger files and combine them" in {
      val (file1, file2) = (new File("blah"))
      swaggerMerger.mergeFiles()
    }
  }
}
