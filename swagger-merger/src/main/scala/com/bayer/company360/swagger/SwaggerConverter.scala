package com.bayer.company360.swagger

import java.io.{File, FileInputStream, InputStreamReader}

import com.bayer.company360.swagger.SwaggerSchema.SwaggerDoc
import io.circe.generic.auto._
import io.circe.syntax._
import io.circe.yaml.{Printer, parser}
import scala.util.matching.Regex.Groups

class SwaggerConverter {
  def parse(file: File): SwaggerDoc = {
    val parsedFile = parser.parse(new InputStreamReader(new FileInputStream(file)))
    parsedFile match {
      case Left(failure) => throw failure
      case Right(json) => json.as[SwaggerDoc].getOrElse(throw new Exception(s"Failed to convert file ${file.getAbsolutePath} to Swagger doc"))
    }
  }

  private def toDecimalNotation(yamlString: String) = {
    import java.text.DecimalFormat
    val format = new DecimalFormat("0.#")
    val scientificNotationPattern = """!!\w+ '(\d+)e(\d+)'""".r
    scientificNotationPattern.replaceAllIn(yamlString, _ match {
      case Groups(coefficient, magnitude) =>
        val rawConversion = (coefficient.toDouble * scala.math.pow(10, magnitude.toDouble))
        format.format(rawConversion)
    })
  }

  def toYaml(swaggerDoc: SwaggerDoc): String = {
    val printed = Printer(preserveOrder = true, indent = 2, dropNullKeys = true)
      .pretty(swaggerDoc.asJson)

    toDecimalNotation(printed)
  }
}
