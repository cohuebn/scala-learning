package com.bayer.company360.swagger.e2e

import better.files.File
import org.scalatest.{Matchers, WordSpecLike}
import com.bayer.company360.swagger.SwaggerMerger

class SwaggerConcatenationSpec extends WordSpecLike with Matchers {
  lazy val inputFilesDirectory = getClass.getClassLoader.getResource("input-files").toURI
  lazy val resourcesDirectory = File(inputFilesDirectory).parent.pathAsString

  "Swagger merger" should {
    "Overlay resource files on top of base file" in {
      val outputFile = SwaggerMerger.mergeFiles(inputFilesDirectory.toString, "*.yaml")

      outputFile.pathAsString should equal (s"$resourcesDirectory/swagger.json")
    }
  }
}
