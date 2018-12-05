ThisBuild / version := "0.0.1-SNAPSHOT"
ThisBuild / scalaVersion := "2.12.6"
ThisBuild / organization := "com.bayer.company360"

lazy val swaggerMerger = (project in file("."))
  .enablePlugins(SbtPlugin)
  .settings(
    name := "swagger-merger",
    sbtPlugin := true,
    libraryDependencies ++= Seq(
      "org.scala-sbt" %% "scripted-plugin" % sbtVersion.value,
      "io.circe" %% "circe-yaml" % "0.9.0",
      "org.scalatest" %% "scalatest" % "3.0.5" % Test
    ),
    scriptedLaunchOpts := { scriptedLaunchOpts.value ++
      Seq("-Xmx1024M", "-Dplugin.version=" + version.value)
    },
    scriptedBufferLog := false
  )