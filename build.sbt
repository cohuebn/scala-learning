ThisBuild / version := "0.0.1-justbeginning"
ThisBuild / scalaVersion := "2.12.6"

lazy val akkaVersion = "2.5.18"

lazy val rootName = "cory-learns-scala"

lazy val akkaDependencies = Seq(
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "com.typesafe.akka" %% "akka-testkit" % akkaVersion,
  "org.scalatest" %% "scalatest" % "3.0.5" % "test"
)

lazy val root = (project in file("."))
  .aggregate(core, console)

lazy val core = project
  .settings(
    name := s"$rootName-core",
    libraryDependencies ++= akkaDependencies
  )

lazy val console = project
  .enablePlugins(PackPlugin)
  .dependsOn(core)
  .settings(
    name := s"$rootName-console",
    libraryDependencies ++= akkaDependencies
  )