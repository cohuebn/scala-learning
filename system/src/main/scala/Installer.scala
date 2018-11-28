package com.cory.system

import java.io.{File => JFile}
import java.net.URL

import better.files._

import scala.language.postfixOps
import scala.sys.process._

object Installer extends App {
  lazy val rootDirectory: File = File(getClass.getClassLoader.getResource("dummy.txt").toURI)
    .parent
    .parent
    .parent

  def ensureDirectoryExists(path: String): Unit = {
    println(s"Ensuring $path exists")
    File(path).createDirectoryIfNotExists(true)
  }

  def runWhenNotFound(path: String)(action: => Unit): Unit = {
    if (File(path).exists) {
      println(s"$path found...skipping action")
    }
    else {
      println(s"$path not found")
      action
    }
  }

  def intallKafka(): Unit = {
    val zipPath = s"${rootDirectory}/kafka.tgz"

    runWhenNotFound(zipPath) {
      println(s"Installing Kafka at $zipPath")
      new URL("http://mirror.reverse.net/pub/apache/kafka/2.1.0/kafka_2.11-2.1.0.tgz") #> new JFile(zipPath)!
    }

    val outputPath = s"${rootDirectory}/kafka"
    runWhenNotFound(outputPath) {
      val outputDirectory = File(outputPath)
      outputDirectory.delete(true)
      println(s"Unzipping $zipPath to $outputPath")
      s"tar -xvzf $zipPath --force-local"!;
      File("kafka_2.11-2.1.0").moveTo(outputDirectory)
    }

    println("Kafka installed")
  }

  ensureDirectoryExists(rootDirectory.pathAsString)
  intallKafka
}