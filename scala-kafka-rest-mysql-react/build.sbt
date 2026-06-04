name := """scala-kafka-rest-mysql-react"""
organization := "com.scala"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.13.18"
val circeVersion = "0.14.1"

libraryDependencies ++= Seq(
  guice,
  "org.scalatestplus.play" %% "scalatestplus-play"   % "7.0.2" % Test,
  "com.mysql"               % "mysql-connector-j"     % "9.1.0",
  "org.flywaydb"            % "flyway-core"           % "10.21.0",
  "org.flywaydb"            % "flyway-mysql" % "10.21.0",
  "org.mindrot"             % "jbcrypt" % "0.4",
  "org.playframework" %% "play-slick" % "6.1.0",
  "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.15.2",
  "com.github.jwt-scala" %% "jwt-circe" % "10.0.0",
  "io.circe" %% "circe-generic" % "0.14.9"
)

// for migration
enablePlugins(FlywayPlugin)
flywayLocations := Seq("filesystem:src/main/resources/db/migration")
flywayUrl := "jdbc:mysql://127.0.0.1:3306/scala_kafka?preserveInstants=true&serverTimezone=UTC"
flywayUser := "rey"
flywayPassword := "rey"

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "com.scala.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "com.scala.binders._"
