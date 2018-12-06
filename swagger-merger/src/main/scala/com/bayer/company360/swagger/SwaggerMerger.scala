package com.bayer.company360.swagger

import java.io.File

import com.bayer.company360.swagger.SwaggerSchema.SwaggerDoc

class SwaggerMerger(swaggerConverter: SwaggerConverter) {
  def mergeFiles(baseFile: File, filesToMerge: Seq[File]): SwaggerDoc = {
    val baseSwagger = swaggerConverter.parse(baseFile)
    val swaggerToMerge = filesToMerge.map(swaggerConverter.parse)

    baseSwagger
  }

//  private def getSwaggerDocument(file: File) = {
//    FileIO.fromPath(file.toPath)
//      .reduce((fileBytes, segmentBytes) => fileBytes.concat(segmentBytes))
//  }
}
