package com.bayer.company360.swagger

import java.io.{File, FileInputStream, InputStreamReader}

import com.bayer.company360.swagger.SwaggerSchema.SwaggerDoc
import io.circe.generic.auto._
import io.circe.syntax._
import io.circe.yaml.{Printer, parser}

import scala.util.matching.Regex.Groups
import scala.util.{Failure, Success, Try}

class SwaggerConverter {
  def parse(file: File): Try[SwaggerDoc] = {
    val parsedFile = parser.parse(new InputStreamReader(new FileInputStream(file)))
        .flatMap(json => json.as[SwaggerDoc])

    parsedFile match {
      case Left(failure) => Failure(new FileException(file, failure))
      case Right(swaggerDoc) => Success(swaggerDoc)
    }
  }

  def toYaml(swaggerDoc: SwaggerDoc): String = {
    Printer(preserveOrder = true, indent = 2, dropNullKeys = true)
      .pretty(swaggerDoc.asJson)
      .scientificNotationToDecimal
  }

  private implicit class YamlString(value: String) {
    private val format = new java.text.DecimalFormat("0.############")
    private val scientificNotationPattern = """(?:!!\w+ ')?(-?\d+)e(-?\d+)(?:')?""".r

    def scientificNotationToDecimal: String = {
      scientificNotationPattern.replaceAllIn(value, _ match {
        case Groups(mantissa, exponent) =>
          val rawConversion = (mantissa.toDouble * scala.math.pow(10, exponent.toDouble))
          format.format(rawConversion)
      })
    }
  }
}
