ThisBuild / version := "0.0.1-SNAPSHOT"
ThisBuild / scalaVersion := "2.12.6"
ThisBuild / organization := "com.bayer.company360"

lazy val versions = new {
  val akka = "2.5.18"
  val circe = "0.10.1"
}

lazy val swaggerMerger = (project in file("."))
  .enablePlugins(SbtPlugin)
  .settings(
    name := "swagger-merger",
    sbtPlugin := true,
    libraryDependencies ++= Seq(
      "org.scala-sbt" %% "scripted-plugin" % sbtVersion.value,
      "io.circe" %% "circe-yaml" % "0.9.0",
      "com.typesafe.akka" %% "akka-stream" % versions.akka,
      "com.typesafe.akka" %% "akka-stream-testkit" % versions.akka % Test,
      "io.circe" %% "circe-core" % versions.circe,
      "io.circe" %% "circe-generic" % versions.circe,
      "org.scaldi" %% "scaldi" % "0.5.8",
      "org.scalatest" %% "scalatest" % "3.0.5" % Test,
      "org.scalamock" %% "scalamock" % "4.1.0" % Test,
      "it.bitbl" %% "scala-faker" % "0.4" % Test
    ),
    scriptedLaunchOpts := { scriptedLaunchOpts.value ++
      Seq("-Xmx1024M", "-Dplugin.version=" + version.value)
    },
    scriptedBufferLog := false
  )