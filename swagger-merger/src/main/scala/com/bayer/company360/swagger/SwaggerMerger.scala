package com.bayer.company360.swagger

import java.io.File

import com.bayer.company360.swagger.SwaggerSchema.SwaggerDoc

import scala.util.{Failure, Success, Try}

class SwaggerMerger(swaggerConverter: SwaggerConverter, swaggerDocAccumulator: SwaggerDocAccumulator) {
  def mergeFiles(baseFile: File, filesToMerge: Seq[File]): Try[SwaggerDoc] = {
    val parseResults = (baseFile +: filesToMerge)
      .par.map(file => (file, swaggerConverter.parse(file)))
      .seq

    val parseFailures = parseResults collect { case (_, Failure(throwable)) => throwable }
    if (parseFailures.nonEmpty)
        Failure(new AggregateThrowable(parseFailures))
    else {
      val successfulParseResults = parseResults collect {
        case (file, Success(swaggerDoc)) =>
          (swaggerDoc, SwaggerDocAccumulator.createAccumulatedSwaggerDoc(file, swaggerDoc)) // TODO - remove tuple (wip until accumulator finished)
      }
      val (swaggerDocs, swaggerAccumulators) = successfulParseResults.unzip
      val accumulatedSwaggerDocs = swaggerAccumulators.reduce(SwaggerDocAccumulator.mergeAccumulators)
      val mergedSwagger = mergeSwaggerDocs(swaggerDocs.head, swaggerDocs.tail)
      Success(mergedSwagger)
    }
  }

  private def mergeSwaggerDocs(baseSwagger: SwaggerDoc, swaggerToMerge: Seq[SwaggerDoc]): SwaggerDoc = {
    swaggerToMerge.fold(baseSwagger)((mergedSwagger, currentSwaggerDoc) =>
      mergedSwagger.copy(
        paths = mergeMaps(mergedSwagger.paths, currentSwaggerDoc.paths),
        definitions = mergeMaps(mergedSwagger.definitions, currentSwaggerDoc.definitions)
      )
    )
  }

  private def mergeMaps[T, V](map1: Map[T, V], map2: Map[T, V]) = {
    map2.foldLeft(map1) { case (aggregateMap: Map[T, V], (key, value)) =>
      aggregateMap.updated(key, value)
    }
  }
}
