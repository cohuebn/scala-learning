package com.bayer.company360.swagger

import java.io.{File, FileInputStream, InputStreamReader}

import com.bayer.company360.swagger.SwaggerSchema.SwaggerDoc
import io.circe.{Json, ParsingFailure}
import io.circe.generic.auto._
import io.circe.syntax._
import io.circe.yaml.{Printer, parser}

import scala.util.{Failure, Success, Try}

class SwaggerConverter {
  def parse(file: File): SwaggerDoc = {
    val parsedFile = parser.parse(new InputStreamReader(new FileInputStream(file)))
    parsedFile match {
      case Left(failure) => throw failure
      case Right(json) => json.as[SwaggerDoc].getOrElse(throw new Exception(s"Failed to convert file ${file.getAbsolutePath} to Swagger doc"))
    }

//    new SwaggerDoc(
//      "2.1",
//      new SwaggerInfo("blah", "blah"),
//      "the-host",
//      List("bogus-scheme"),
//      "/thebase/path/v1",
//      1,
//      List("application/json"),
//      Map(),
//      Map()
//    )
  }

  def toYaml(swaggerDoc: SwaggerDoc): String = {
    Printer(preserveOrder = true, indent = 2).pretty(swaggerDoc.asJson)
  }
}
