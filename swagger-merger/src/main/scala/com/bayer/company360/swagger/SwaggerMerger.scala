package com.bayer.company360.swagger

import java.io.File

import com.bayer.company360.swagger.SwaggerSchema.SwaggerDoc

class SwaggerMerger(swaggerConverter: SwaggerConverter) {
  def mergeFiles(baseFile: File, filesToMerge: Seq[File]): SwaggerDoc = {
    val baseSwagger = swaggerConverter.parse(baseFile)
    val swaggerToMerge = filesToMerge.map(swaggerConverter.parse)

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
