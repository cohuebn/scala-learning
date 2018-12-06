package com.bayer.company360.swagger.test

import com.bayer.company360.swagger.SwaggerSchema._
import faker._

import scala.util.Random

object DataGenerator {
  def swaggerDoc(swagger: Option[String] = None,
                 info: Option[SwaggerInfo] = None,
                 host: Option[String] = None,
                 schemes: Option[List[String]] = None,
                 basePath: Option[String] = None,
                 `x-what-is-maximum-number-of-records-that-could-be-returned`: Option[Int] = None,
                 produces: Option[List[String]] = None,
                 paths: Option[Map[PathName, SwaggerPath]] = None,
                 definitions: Option[Map[DefinitionName, SwaggerDefinition]] = None) = {
    new SwaggerDoc(
      swagger.getOrElse("2.0"),
      info.getOrElse(swaggerInfo()),
      host.getOrElse(Internet.domain_name),
      schemes.getOrElse(List("bogus-scheme")),
      basePath.getOrElse("/thebase/path/v1"),
      `x-what-is-maximum-number-of-records-that-could-be-returned`.getOrElse(Random.nextInt()),
      produces.getOrElse(List("application/json")),
      paths.getOrElse(Map()),
      definitions.getOrElse(Map())
    )
  }

  def swaggerInfo(title: Option[String] = None, version: Option[String] = None) = {
    new SwaggerInfo(
      title.getOrElse(Lorem.sentence()),
      version.getOrElse("0.0.1-dontcare")
    )
  }
}
