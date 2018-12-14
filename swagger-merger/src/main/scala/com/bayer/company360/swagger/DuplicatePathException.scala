package com.bayer.company360.swagger

import java.io.File

import com.bayer.company360.swagger.StringExtender._
import com.bayer.company360.swagger.SwaggerSchema.PathName

case class DuplicatePathException(pathName: PathName, files: Seq[File]) extends Exception {
  override def getMessage: String = {
    val fileNames = files.map(file => s"- ${file.getCanonicalPath}").mkString(newLine).tabIndentLines
    s"The path $pathName has conflicts across the following files:$newLine$fileNames"
  }
}


