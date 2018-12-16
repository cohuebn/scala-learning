package com.bayer.company360.swagger

import io.circe.Json

object SwaggerSchema {
  type PathName = String
  type DefinitionName = String
  type PropertyName = String
  type ResponseCode = Int
  type SwaggerExamples = Map[String, Json]

  trait SwaggerBase {
    val swagger: String
    val info: SwaggerInfo
    val host: String
    val schemes: List[String]
    val basePath: String
    val `x-what-is-maximum-number-of-records-that-could-be-returned`: Int
    val produces: List[String]
  }

  case class SwaggerDoc(swagger: String, info: SwaggerInfo, host: String,
                        schemes: List[String], basePath: String,
                        `x-what-is-maximum-number-of-records-that-could-be-returned`: Int,
                        produces: List[String],
                        paths: Map[PathName, SwaggerPath],
                        definitions: Map[DefinitionName, SwaggerDefinition]) extends SwaggerBase

  case class SwaggerInfo(title: String,
                         description: Option[String],
                         version: String)

  case class SwaggerPath(get: Option[SwaggerOperation],
                         post: Option[SwaggerOperation]) {

    def operationNameAndValue: (String, SwaggerOperation) = (get, post) match {
      case (Some(_), Some(_)) => throw new RuntimeException(s"all SwaggerPath objects must have either a get or a post SwaggerOperation, but not both")
      case (_, Some(postOp)) => ("post", postOp)
      case (Some(getOp), _) => ("get", getOp)
      case (None, None) => throw new RuntimeException(s"all SwaggerPath objects must have either a get or post SwaggerOperation")
    }
  }

  case class SwaggerDefinition(`type`: Option[String],
                               format: Option[String],
                               required: Option[List[String]],
                               properties: Option[Map[PropertyName, SwaggerProperty]],
                               `$ref`: Option[String])

  case class SwaggerOperation(summary: Option[String],
                              description: Option[String],
                              operationId: Option[String],
                              tags: Option[List[String]],
                              parameters: List[SwaggerParameter],
                              responses: Map[ResponseCode, SwaggerResponse])

  case class SwaggerProperty(description: Option[String],
                             `type`: Option[String],
                             format: Option[String],
                             items: Option[SwaggerRef],
                             `$ref`: Option[DefinitionName])

  case class SwaggerParameter(name: String, in: String,
                              description: Option[String],
                              required: Option[Boolean],
                              `type`: Option[String],
                              format: Option[String],
                              items: Option[SwaggerRef] = None,
                              schema: Option[SwaggerResponseSchema] = None,
                              collectionFormat: Option[String] = None,
                              `x-example`: Option[String] = None)

  case class SwaggerResponse(description: Option[String], schema: SwaggerResponseSchema, examples: Option[SwaggerExamples])

  case class SwaggerResponseSchema(properties: Option[Map[PropertyName, SwaggerProperty]], `type`: Option[String], `$ref`: Option[String])

  case class SwaggerRef(`$ref`: Option[DefinitionName], `type`: Option[String], format: Option[String])
}