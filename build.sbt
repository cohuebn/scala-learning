lazy val install = taskKey[Unit]("install")
lazy val sendKafkaData = taskKey[Unit]("sendKafkaData")

ThisBuild / version := "0.0.2"
ThisBuild / scalaVersion := "2.12.6"
ThisBuild / organization := "com.bayer.company360"
ThisBuild / resolvers += "Fabricator" at "http://dl.bintray.com/biercoff/Fabricator"

lazy val akkaVersion = "2.5.18"
lazy val circeVersion = "0.10.1"

lazy val rootName = "cory-learns-scala"

lazy val akkaDependencies = Seq(
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "com.typesafe.akka" %% "akka-testkit" % akkaVersion % Test,
  "org.scalatest" %% "scalatest" % "3.0.5" % Test
)

lazy val streamsDependencies = akkaDependencies ++ Seq(
  "com.typesafe.akka" %% "akka-stream" % akkaVersion
)

lazy val kafkaDependencies = streamsDependencies ++
  Seq(
    "com.typesafe.akka" %% "akka-stream-kafka" % "0.21.1",
    "ch.qos.logback" % "logback-classic" % "1.2.3"
  )

lazy val root = (project in file("."))
  .aggregate(system, core, console, web)

lazy val playground = project
  .settings(
    name := s"$rootName-playground",
    libraryDependencies ++= akkaDependencies,
    libraryDependencies ++= streamsDependencies,
    libraryDependencies ++= Seq(
      "org.typelevel" %% "cats-core" % "1.5.0",
      "io.circe" %% "circe-core" % circeVersion,
      "io.circe" %% "circe-parser" % circeVersion,
      "io.circe" %% "circe-jawn" % circeVersion,
      "io.circe" %% "circe-generic" % circeVersion,
      "io.circe" %% "circe-yaml" % "0.9.0",
      "com.chuusai" %% "shapeless" % "2.3.3",
    )
  )

lazy val system = project
  .dependsOn(core)
  .settings(
    name := s"$rootName-system",
    fullRunTask(install, Compile, "com.cory.system.Installer"),
    fullRunTask(sendKafkaData, Compile, "com.cory.system.SendKafkaData"),
    libraryDependencies += "com.github.pathikrit" %% "better-files" % "3.6.0",
    libraryDependencies += "com.github.azakordonets" %% "fabricator" % "2.1.5",
    libraryDependencies += "org.apache.commons" % "commons-lang3" % "3.8.1",
    libraryDependencies ++= kafkaDependencies
  )

lazy val core = project
  .settings(
    name := s"$rootName-core",
    libraryDependencies ++= kafkaDependencies
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
    libraryDependencies ++= kafkaDependencies,
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-http" % "10.1.5",
      "com.typesafe.akka" %% "akka-http-spray-json" % "10.1.5",
      "io.circe" %% "circe-core" % circeVersion,
      "io.circe" %% "circe-jawn" % circeVersion,
      "io.circe" %% "circe-generic" % circeVersion,
      "de.heikoseeberger" %% "akka-http-circe" % "1.22.0"
    )
  )