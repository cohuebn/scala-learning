package com.bayer.company360.swagger

import java.io.File

class FileException(file: File, innerThrowable: Throwable) extends Exception {
  override def getMessage: String = {
    s"An error occurred while processing file ${file.getCanonicalPath}:\n${innerThrowable.getMessage}"
  }

  override def getCause: Throwable = innerThrowable
}


