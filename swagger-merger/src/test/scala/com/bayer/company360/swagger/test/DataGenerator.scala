package com.bayer.company360.swagger.test

import com.bayer.company360.swagger.SwaggerSchema._
import com.bayer.company360.swagger.test.DataGenerator.HttpMethod.HttpMethod
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
                 definitions: Option[Map[DefinitionName, SwaggerDefinition]] = None): SwaggerDoc = {
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

  def swaggerInfo(title: Option[String] = None, description: Option[String] = None, version: Option[String] = None) = {
    new SwaggerInfo(
      title.getOrElse(Lorem.sentence()),
      description,
      version.getOrElse("0.0.1-dontcare")
    )
  }

  def swaggerPath(method: HttpMethod,
                  summary: Option[String] = None,
                  description: Option[String] = None,
                  operationId: Option[String] = None,
                  tags: Option[List[String]] = None,
                  parameters: Option[List[SwaggerParameter]] = None,
                  responses: Option[Map[ResponseCode, SwaggerResponse]] = None): SwaggerPath = {
    val operation: SwaggerOperation = new SwaggerOperation(
      summary,
      description,
      operationId,
      tags,
      parameters.getOrElse(List()),
      responses.getOrElse(Map())
    )
    method match {
      case HttpMethod.Get => new SwaggerPath(Option(operation), None)
      case HttpMethod.Post => new SwaggerPath(None, Option(operation))
    }
  }

  def swaggerDefinition(`type`: Option[String] = None,
                        properties: Option[Map[PropertyName, SwaggerProperty]] = None,
                       `$ref`: Option[String] = None): SwaggerDefinition = {
    new SwaggerDefinition(
      `type`,
      None, // 'required' appears to be unused
      properties,
      `$ref`
    )
  }

  object HttpMethod extends Enumeration {
    type HttpMethod = Value
    val Get, Post = Value
  }
}
