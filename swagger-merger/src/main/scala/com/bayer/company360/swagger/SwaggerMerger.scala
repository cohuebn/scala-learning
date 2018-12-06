package com.bayer.company360.swagger

import java.io.File

import scaldi.Injectable

class SwaggerMerger(swaggerFileParser: SwaggerFileParser) {
  def mergeFiles(files: Seq[File]): String = {
    val fileSources = files.map(swaggerFileParser.parse)
    "TODO"
  }

//  private def getSwaggerDocument(file: File) = {
//    FileIO.fromPath(file.toPath)
//      .reduce((fileBytes, segmentBytes) => fileBytes.concat(segmentBytes))
//  }
}
