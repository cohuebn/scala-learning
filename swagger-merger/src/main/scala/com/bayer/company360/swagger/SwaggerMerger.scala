package com.bayer.company360.swagger

import java.io.File

import com.bayer.company360.swagger.SwaggerSchema.SwaggerDoc

import scala.collection.parallel.ParSeq
import scala.util.{Failure, Success, Try}

class SwaggerMerger(swaggerConverter: SwaggerConverter) {
  def mergeFiles(baseFile: File, filesToMerge: Seq[File]): Try[SwaggerDoc] = {
    val parseResults = parseFiles(baseFile +: filesToMerge)
    parseResults.flatMap { swaggerDocs =>
      val accumulatedPaths = swaggerDocs.map(doc => toAccumulated(doc.file, doc.value.paths))
        .reduceLeft(mergeAccumulated)

      val duplicatedPathExceptions = getDuplicates(accumulatedPaths)
        .map { case (pathName, paths) => DuplicatePathException(pathName, paths.map(_.file)) }

      val accumulatedDefinitions = swaggerDocs.map(doc => toAccumulated(doc.file, doc.value.definitions))
        .reduceLeft(mergeAccumulated)

      val duplicatedDefinitionsExceptions = getDuplicates(accumulatedDefinitions)
        .map { case (definitionName, definitions) => DuplicatePathException(definitionName, definitions.map(_.file)) }

      if (duplicatedPathExceptions.nonEmpty || duplicatedDefinitionsExceptions.nonEmpty) {
        Failure(new AggregateThrowable(duplicatedPathExceptions ++ duplicatedDefinitionsExceptions))
      }
      else {
        val merged = swaggerDocs.head.value.copy(
          paths = accumulatedPaths.map { case (pathName, paths) => pathName -> paths.head.value },
          definitions = accumulatedDefinitions.map { case (name, definition) => name -> definition.head.value }
        )
        Success(merged)
      }
    }
  }

  private def parseFiles(files: Seq[File]): Try[ParSeq[Traceable[SwaggerDoc]]] = {
    val parseResults = files.par.map(file => (file, swaggerConverter.parse(file)))
    val failures = parseResults collect { case (_, Failure(throwable)) => throwable}
    if (failures.nonEmpty)
      Failure(new AggregateThrowable(failures.seq))
    else {
      val traceableSwaggerDocs = parseResults collect {
        case (file, Success(swaggerDoc)) => Traceable(file, swaggerDoc)
      }
      Success(traceableSwaggerDocs)
    }
  }

  private def toAccumulated[Key, Value](file: File, items: Map[Key, Value]): Map[Key, Seq[Traceable[Value]]] = {
    items map { case (key, value) => key -> Seq(Traceable(file, value)) }
  }

  private def mergeAccumulated[Key, Value](accumulated1: Map[Key, Seq[Value]], accumulated2: Map[Key, Seq[Value]]): Map[Key, Seq[Value]] = {
    accumulated2.foldLeft(accumulated1) {
      case (allAccumulated, (key, value)) => allAccumulated.updated(key, allAccumulated.getOrElse(key, Seq()) ++ value)
    }
  }

  private def getDuplicates[Key, T](accumulated: Map[Key, Seq[Traceable[T]]]): Map[Key, Seq[Traceable[T]]] = {
    accumulated filter { case (_, value) => value.length > 1 }
  }
}
