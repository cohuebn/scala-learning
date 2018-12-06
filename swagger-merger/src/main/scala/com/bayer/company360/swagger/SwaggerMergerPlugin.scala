package com.bayer.company360.swagger

import sbt._
import Keys._
import AppInjector.injector
import scaldi.Injectable._

object SwaggerMergerPlugin extends AutoPlugin {
  object autoImport {
    val mergeSwaggerInputFilter = settingKey[FileFilter]("Determines which files should be merged together")
    val mergeSwaggerBaseFile = settingKey[FileFilter]("Which file should be used as the base file? The first file matching the pattern will be used")
    val mergeSwaggerPrintInputs = settingKey[Boolean]("Should all located input files be printed before generating output?")
    val mergeSwaggerOutputFilename = settingKey[String]("The name of the generated Swagger file")
    val mergeSwagger = inputKey[Unit]("Merges multiple Swagger documents into one")
  }

  import autoImport._
  override lazy val projectSettings = Seq(
    mergeSwaggerInputFilter := "*.yaml",
    mergeSwaggerBaseFile := "api.yaml",
    mergeSwaggerPrintInputs := false,
    mergeSwaggerOutputFilename := "api.swagger.yaml",
    mergeSwagger := {
      val baseSwaggerFileFinder = (baseDirectory.value ** mergeSwaggerBaseFile.value)
      val swaggerFileFilter = (baseDirectory.value ** mergeSwaggerInputFilter.value)

      val baseFile = baseSwaggerFileFinder.get.head
      val filesToMerge = swaggerFileFilter.get.filter(file => file != baseFile)

      if (mergeSwaggerPrintInputs.value) {
        println(s"The following files will be merged to create ${mergeSwaggerOutputFilename.value}")
        println(s"\t- ${baseFile.getCanonicalPath} (base file)")
        filesToMerge.foreach(x => println(s"\t- ${x.getCanonicalPath}"))
      }

      val generatedFile = (resourceManaged in mergeSwagger).value / mergeSwaggerOutputFilename.value
      val mergedSwagger = inject[SwaggerMerger].mergeFiles(baseFile, filesToMerge)
      val mergedSwaggerYaml = inject[SwaggerConverter].toYaml(mergedSwagger)
      IO.write(generatedFile, mergedSwaggerYaml)
      IO.write(new File("c:/tmp/test.yaml"), mergedSwaggerYaml) // TODO - remove after original dev done
      println(s"Generated file ${generatedFile}")
    }
  )
}
