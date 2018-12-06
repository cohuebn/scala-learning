package com.bayer.company360.swagger

import java.io.File

import com.bayer.company360.swagger.SwaggerSchema.{SwaggerDoc, SwaggerInfo}
import io.circe.generic.auto._
import io.circe.syntax._
import io.circe.yaml.Printer

class SwaggerConverter {
  def parse(file: File): SwaggerSchema.SwaggerDoc = {
    new SwaggerDoc(
      "2.0",
      new SwaggerInfo("blah", "blah"),
      "the-host",
      List("bogus-scheme"),
      "/thebase/path/v1",
      1,
      List("application/json"),
      Map(),
      Map()
    )
  }

  def toYaml(swaggerDoc: SwaggerDoc): String = {
    Printer(preserveOrder = true, indent = 2).pretty(swaggerDoc.asJson)
  }
}
