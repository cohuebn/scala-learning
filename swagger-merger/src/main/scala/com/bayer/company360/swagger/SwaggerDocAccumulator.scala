package com.bayer.company360.swagger

import java.io.File

import com.bayer.company360.swagger.SwaggerSchema._

import scala.util.{Success, Try}

case class Traceable[T](file: File, value: T)

case class AccumulatedSwaggerDoc(swagger: String, info: SwaggerInfo, host: String,
                                 schemes: List[String], basePath: String,
                                 `x-what-is-maximum-number-of-records-that-could-be-returned`: Int,
                                 produces: List[String],
                                 paths: Map[PathName, Seq[Traceable[SwaggerPath]]],
                                 definitions: Map[DefinitionName, Seq[Traceable[SwaggerDefinition]]])
  extends SwaggerBase {}

trait SwaggerDocAccumulator {
  def createAccumulatedSwaggerDoc(file: File, swaggerDoc: SwaggerDoc): AccumulatedSwaggerDoc
  def mergeAccumulators(doc1: AccumulatedSwaggerDoc, doc2: AccumulatedSwaggerDoc): AccumulatedSwaggerDoc
  def toSwaggerDoc(accumulated: AccumulatedSwaggerDoc): Try[SwaggerDoc]
}

object SwaggerDocAccumulator extends SwaggerDocAccumulator {
  def createAccumulatedSwaggerDoc(file: File, swaggerDoc: SwaggerDoc): AccumulatedSwaggerDoc = {
    def toTraceable[T](value: T) = new Traceable[T](file, value)

    new AccumulatedSwaggerDoc(
      swaggerDoc.swagger,
      swaggerDoc.info,
      swaggerDoc.host,
      swaggerDoc.schemes,
      swaggerDoc.basePath,
      swaggerDoc.`x-what-is-maximum-number-of-records-that-could-be-returned`,
      swaggerDoc.produces,
      swaggerDoc.paths.map {
        case (pathName, path) => pathName -> Seq(toTraceable(path))
      },
      swaggerDoc.definitions.map {
        case (definitionName, definition) => definitionName -> Seq(toTraceable(definition))
      }
    )
  }

  def mergeAccumulators(doc1: AccumulatedSwaggerDoc, doc2: AccumulatedSwaggerDoc): AccumulatedSwaggerDoc = {
    doc1.copy(
      paths = mergeMaps(doc1.paths, doc2.paths),
      definitions = mergeMaps(doc1.definitions, doc2.definitions)
    )
  }

  def toSwaggerDoc(accumulated: AccumulatedSwaggerDoc): Try[SwaggerDoc] = {
    Success(new SwaggerDoc(
      accumulated.swagger,
      accumulated.info,
      accumulated.host,
      accumulated.schemes,
      accumulated.basePath,
      accumulated.`x-what-is-maximum-number-of-records-that-could-be-returned`,
      accumulated.produces,
      accumulated.paths.map {
        case (pathName, paths) => pathName -> paths.head.value
      },
      accumulated.definitions.map {
        case (definitionName, definitions) => definitionName -> definitions.head.value
      }
    ))
  }

  private def mergeMaps[T](map1: Map[String, Seq[T]], map2: Map[String, Seq[T]]) = {
    map2.foldLeft(map1) { case (aggregateMap: Map[String, Seq[T]], (key, value)) =>
      aggregateMap.updated(key, map1.getOrElse(key, Seq()) ++ value)
    }
  }
}
