package com.cory.system

import java.io.{File => JFile}
import java.net.URL
import java.nio.file.attribute.PosixFilePermission

import better.files._
import org.apache.commons.lang3.SystemUtils

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
      val tarCommand = s"tar -xvzf $zipPath"
      val osSpecificTarCommand = if (SystemUtils.IS_OS_WINDOWS) s"$tarCommand --force-local" else tarCommand
      osSpecificTarCommand!;
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

  def moveShellRunnerToRoot() = {
    val shellRunner = resourceAsFile("run.sh")
    println(s"Ensuring correct permissions on $shellRunner")
    grantFullPermission(shellRunner)
    moveToRoot(shellRunner)
    None
  }

  def moveToRoot(file: File) = {
    println(s"Moving $file to $rootDirectory")
    val destination = File(s"$rootDirectory/${file.name}")
    if (destination.exists) destination.delete()

    file.moveToDirectory(rootDirectory)
    None
  }

  ensureDirectoryExists(rootDirectory.pathAsString)
  installKafka()
  if (SystemUtils.IS_OS_WINDOWS) moveToRoot(powershellRunner) else moveShellRunnerToRoot()
}