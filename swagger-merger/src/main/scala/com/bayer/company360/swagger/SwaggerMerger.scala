package com.bayer.company360.swagger

import java.io.File

import com.bayer.company360.swagger.SwaggerSchema.SwaggerDoc

import scala.util.{Failure, Success, Try}

class SwaggerMerger(swaggerConverter: SwaggerConverter) {
  def mergeFiles(baseFile: File, filesToMerge: Seq[File]): Try[SwaggerDoc] = {
    val parseResults = (baseFile +: filesToMerge).map(file => swaggerConverter.parse(file))
    val parseFailures = parseResults collect { case Failure(throwable) => throwable }

    if (parseFailures.nonEmpty)
        Failure(new AggregateThrowable(parseFailures))
    else {
      val allSwagger = parseResults collect { case Success(swaggerDoc) => swaggerDoc }
      val (baseSwagger, swaggerToMerge) = (allSwagger.head, allSwagger.tail)

      val mergedDoc = swaggerToMerge.fold(baseSwagger)((mergedSwagger, currentSwaggerDoc) =>
        mergedSwagger.copy(
          paths = mergeMaps(mergedSwagger.paths, currentSwaggerDoc.paths),
          definitions = mergeMaps(mergedSwagger.definitions, currentSwaggerDoc.definitions)
        )
      )

      Success(mergedDoc)
    }
  }

  private def mergeMaps[T, V](map1: Map[T, V], map2: Map[T, V]) = {
    map2.foldLeft(map1) { case (aggregateMap: Map[T, V], (key, value)) =>
      aggregateMap.updated(key, value)
    }
  }
}
