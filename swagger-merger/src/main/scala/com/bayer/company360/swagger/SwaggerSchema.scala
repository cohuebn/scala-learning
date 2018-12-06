package com.bayer.company360.swagger

object SwaggerSchema {
  type PathName = String
  type DefinitionName = String
  type PropertyName = String
  type ResponseCode = Int

  case class SwaggerDoc(info: SwaggerInfo,
                        host: String,
                        schemes: List[String],
                        basePath: String,
                        produces: List[String],
                        paths: Map[PathName, SwaggerPath],
                        definitions: Map[DefinitionName, SwaggerDefinition])

  case class SwaggerInfo(title: String,
                         version: String)

  case class SwaggerPath(get: Option[SwaggerOperation],
                         post: Option[SwaggerOperation]) {

    def operationNameAndValue: (String,SwaggerOperation) = (get, post) match {
      case (Some(_), Some(_)) => throw new RuntimeException(s"all SwaggerPath objects must have either a get or a post SwaggerOperation, but not both")
      case (_, Some(postOp)) => ("post",postOp)
      case (Some(getOp), _) => ("get",getOp)
      case (None, None) => throw new RuntimeException(s"all SwaggerPath objects must have either a get or post SwaggerOperation")
    }
  }

  case class SwaggerDefinition(`type`: Option[String],
                               required: Option[List[String]],
                               properties: Option[Map[PropertyName, SwaggerProperty]],
                               `$ref`: Option[String])

  case class SwaggerOperation(parameters: List[SwaggerParameter],
                              responses: Map[ResponseCode, SwaggerResponse])

  case class SwaggerProperty(`type`: Option[String],
                             format: Option[String],
                             items: Option[SwaggerRef],
                             `$ref`: Option[DefinitionName])

  case class SwaggerParameter(
                               name: String, // "globalFiscalYear", "limit", "offset", "rowUpdateTimestamp", etc
                               in: String, // GETs have many parameters, each having "in:query".  POSTs have one parameter, with "in: body" and a body definition
                               required: Option[Boolean],

                               // only if in != "body"
                               format: Option[String], // "ISO 4217", "YYYY-MM-DD", ...     or None if format is simply a basic string or number
                               `type`: Option[String], // "string", "integer", "number", etc

                               // only if in == "array"
                               items: Option[SwaggerRef] = None,

                               // only if in == "body"
                               schema: Option[SwaggerSchema] = None,

                               // can be: csv, ssv, tsv, pipes, multi (e.g., p=7&p=9)
                               //This apparently isn't used right now by those who create Swagger YAML, but it could be used to specify that multiple values can be passed for a parameter in (e.g. customerNumber=1&customerNumber=2). probably only applies to gets
                               collectionFormat: Option[String] = None
                             )

  case class SwaggerResponse(schema: SwaggerSchema)

  case class SwaggerSchema(properties: Option[Map[PropertyName, SwaggerProperty]], `type`: Option[String], `$ref`: Option[String])

  case class SwaggerRef(`$ref`: Option[DefinitionName], `type`: Option[String], format: Option[String])
}