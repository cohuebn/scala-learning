package com.cory.system

import java.io.{File => JFile}
import java.net.URL
import java.nio.file.attribute.{PosixFilePermission, PosixFilePermissions}

import better.files._

import scala.language.postfixOps
import scala.sys.process._

object Installer extends App {
  lazy val powershellRunner = resourceAsFile("run.ps1")
  lazy val rootDirectory: File = powershellRunner.parent.parent.parent

  def resourceAsFile(resourceName: String): File = {
    File(getClass.getClassLoader.getResource(resourceName).toURI)
  }

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
      s"tar -xvzf $zipPath"!;
      File("kafka_2.11-2.1.0").moveTo(outputDirectory)
    }

    println("Kafka installed")
  }

  def grantFullPermission(file: File): Unit = {
    val allPermissions = Seq(
      PosixFilePermission.OWNER_EXECUTE,
      PosixFilePermission.OWNER_READ,
      PosixFilePermission.OTHERS_WRITE,
      PosixFilePermission.GROUP_EXECUTE,
      PosixFilePermission.GROUP_READ,
      PosixFilePermission.GROUP_WRITE,
      PosixFilePermission.OTHERS_EXECUTE,
      PosixFilePermission.OTHERS_READ,
      PosixFilePermission.OTHERS_WRITE
    )

    allPermissions.foreach(file.addPermission(_))
  }

  def moveRunnersToRoot(): Unit = {
    val shellRunner = resourceAsFile("run.sh")
    Seq(powershellRunner, shellRunner)
      .filter(_.exists)
      .foreach { file =>
        println(s"Ensuring correct permissions on $file")
        grantFullPermission(file)
        println(s"Moving $file to $rootDirectory")
        File(s"$rootDirectory/${file.name}").delete(true)
        file.moveToDirectory(rootDirectory)
      }
  }

  ensureDirectoryExists(rootDirectory.pathAsString)
  installKafka
  moveRunnersToRoot
}