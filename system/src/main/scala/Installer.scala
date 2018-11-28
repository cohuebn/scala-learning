package com.cory.system

import java.io.{File => JFile}
import java.net.URL

import better.files.File.LinkOptions
import better.files._

import scala.language.postfixOps
import scala.sys.process._

object Installer extends App {
  lazy val runnerScript = File(getClass.getClassLoader.getResource("run.ps1").toURI)
  lazy val rootDirectory: File = runnerScript.parent.parent.parent

  def ensureDirectoryExists(path: String): Unit = {
    println(s"Ensuring $path exists")
    File(path).createDirectoryIfNotExists(true)
  }

  def whenNotFound(path: String)(action: => Unit): Unit = {
    if (File(path).exists) {
      println(s"$path found...skipping action")
    }
    else {
      println(s"$path not found")
      action
    }
  }

  def installKafka(): Unit = {
    val zipPath = s"${rootDirectory}/kafka.tgz"

    whenNotFound(zipPath) {
      println(s"Installing Kafka at $zipPath")
      new URL("http://mirror.reverse.net/pub/apache/kafka/2.1.0/kafka_2.11-2.1.0.tgz") #> new JFile(zipPath)!
    }

    val outputPath = s"${rootDirectory}/kafka"
    whenNotFound(outputPath) {
      val outputDirectory = File(outputPath)
      outputDirectory.delete(true)
      println(s"Unzipping $zipPath to $outputPath")
      s"tar -xvzf $zipPath --force-local"!;
      File("kafka_2.11-2.1.0").moveTo(outputDirectory)
    }

    println("Kafka installed")
  }

  def moveRunnerToRoot(): Unit = {
    println(s"Moving runner to $rootDirectory")
    File(s"$rootDirectory/${runnerScript.name}").delete(true)
    runnerScript.moveToDirectory(rootDirectory)
  }

  ensureDirectoryExists(rootDirectory.pathAsString)
  installKafka
  moveRunnerToRoot
}