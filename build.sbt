lazy val install = taskKey[Unit]("install")

ThisBuild / version := "0.0.2"
ThisBuild / scalaVersion := "2.12.6"

lazy val akkaVersion = "2.5.18"

lazy val rootName = "cory-learns-scala"

lazy val akkaDependencies = Seq(
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "com.typesafe.akka" %% "akka-testkit" % akkaVersion % Test,
  "org.scalatest" %% "scalatest" % "3.0.5" % Test
)

lazy val webDependencies = Seq(
  "com.typesafe.akka" %% "akka-stream" % akkaVersion,
  "com.typesafe.akka" %% "akka-http" % "10.1.5",
  "com.typesafe.akka" %% "akka-http-spray-json" % "10.1.5"
)

lazy val root = (project in file("."))
  .aggregate(system, core, console, web)

lazy val system = project
  .settings(
    name := s"$rootName-system",
    fullRunTask(install, Compile, "com.cory.system.Installer"),
    libraryDependencies += "com.github.pathikrit" %% "better-files" % "3.6.0"
  )

lazy val core = project
  .settings(
    name := s"$rootName-core",
    libraryDependencies ++= akkaDependencies
  )

lazy val console = project
  .dependsOn(core % "compile->compile;test->test")
  .settings(
    name := s"$rootName-console",
    libraryDependencies ++= akkaDependencies
  )

lazy val web = project
  .dependsOn(core % "compile->compile;test->test")
  .settings(
    name := s"$rootName-web",
    libraryDependencies ++= akkaDependencies,
    libraryDependencies ++= webDependencies
  )