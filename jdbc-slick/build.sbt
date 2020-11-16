import java.util.Calendar

import sbt.Keys.{javacOptions, scalacOptions}

ThisBuild / scalaVersion := "2.13.3"
ThisBuild / organization := "com.stulsoft"
ThisBuild / version := "1.0.0"

Compile / packageBin / packageOptions += Package.ManifestAttributes("Build-Date" -> Calendar.getInstance().getTime.toString)

lazy val loggingVersion = "2.13.3"
lazy val scalatestVersion = "3.2.2"

lazy val app = (project in file("."))
  .settings(
    name := "jdbc-slick",
    libraryDependencies += "com.typesafe.akka" %% "akka-stream" % "2.6.10",
    libraryDependencies += "com.lightbend.akka" %% "akka-stream-alpakka-slick" % "2.0.2",
    libraryDependencies += "mysql" % "mysql-connector-java" % "8.0.22",
    libraryDependencies += "com.microsoft.sqlserver" % "mssql-jdbc" % "8.4.1.jre11",
    libraryDependencies += "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2",
    libraryDependencies += "org.apache.logging.log4j" % "log4j-api" % loggingVersion,
    libraryDependencies += "org.apache.logging.log4j" % "log4j-core" % loggingVersion,
    libraryDependencies += "org.apache.logging.log4j" % "log4j-slf4j-impl" % loggingVersion,
    libraryDependencies += "org.scalactic" %% "scalactic" % scalatestVersion,
    libraryDependencies += "org.scalatest" %% "scalatest" % scalatestVersion % Test,
    javacOptions ++= Seq("-source", "11"),
    scalacOptions ++= Seq(
      "-feature",
      "-deprecation",
      "-language:implicitConversions",
      "-language:postfixOps")
  )
