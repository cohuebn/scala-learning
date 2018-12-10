package com.bayer.company360.swagger

import java.io.{File, FileInputStream, InputStreamReader}

import com.bayer.company360.swagger.SwaggerSchema.SwaggerDoc
import com.bayer.company360.swagger.SwaggerSchema._
import io.circe.generic.auto._
import io.circe.syntax._
import io.circe.yaml.{Printer, parser}

class SwaggerConverter {
  def parse(file: File): SwaggerDoc = {
    val parsedFile = parser.parse(new InputStreamReader(new FileInputStream(file)))
    parsedFile match {
      case Left(failure) => throw failure
      case Right(json) => json.as[SwaggerDoc].getOrElse(throw new Exception(s"Failed to convert file ${file.getAbsolutePath} to Swagger doc"))
    }
  }

  def toYaml(swaggerDoc: SwaggerDoc): String = {
    Printer(preserveOrder = true, indent = 2, dropNullKeys = true).pretty(swaggerDoc.asJson)
  }
}
