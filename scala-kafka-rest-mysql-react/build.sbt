name := """scala-kafka-rest-mysql-react"""
organization := "com.scala"

version := "1.0-SNAPSHOT"

javaOptions += "-Djava.awt.headless=true"
// run / fork := true
// test / fork := true


PlayKeys.devSettings += "java.awt.headless" -> "true"


lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.13.18"
val circeVersion = "0.14.1"
val SlickVersion = "3.5.1"

val AkkaVersion = "2.6.20"
val AkkaHttpVersion = "10.2.10"

libraryDependencies ++= Seq(
  guice,
  "com.typesafe.slick" %% "slick"          % SlickVersion,
  "com.typesafe.slick" %% "slick-hikaricp" % SlickVersion,  
  "org.playframework" %% "play-slick" % "6.1.0",

  "com.typesafe.akka" %% "akka-actor-typed" % AkkaVersion,
  "com.typesafe.akka" %% "akka-stream"      % AkkaVersion,
  "com.typesafe.akka" %% "akka-http"        % AkkaHttpVersion,
  "com.typesafe.akka" %% "akka-http-spray-json" % AkkaHttpVersion,

  "org.apache.kafka" % "kafka-clients" % "3.6.1",
  "com.github.fd4s" %% "fs2-kafka" % "3.5.0",
  "org.typelevel"   %% "cats-effect" % "3.5.4",

  "org.scalatestplus.play" %% "scalatestplus-play"   % "7.0.2" % Test,
  "com.mysql"               % "mysql-connector-j"     % "9.1.0",
  "org.flywaydb"            % "flyway-core"           % "10.21.0",
  "org.flywaydb"            % "flyway-mysql" % "10.21.0",
  "org.mindrot"             % "jbcrypt" % "0.4",
  "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.15.2",
  "com.github.jwt-scala" %% "jwt-circe" % "10.0.0",
  "io.circe" %% "circe-generic" % "0.14.9",
  "com.google.zxing" % "core" % "3.5.3",
  "com.google.zxing" % "javase" % "3.5.3",
  "org.typelevel" %% "cats-effect" % "3.5.4",
  "dev.samstevens.totp" % "totp" % "1.7.1",
  "commons-io" % "commons-io" % "2.16.1"
)
resolvers += "Confluent Maven Repository" at "https://confluent.io"

// for migration
enablePlugins(FlywayPlugin)
flywayLocations := Seq("filesystem:src/main/resources/db/migration")
flywayUrl := "jdbc:mysql://127.0.0.1:3306/scala_kafka?preserveInstants=true&serverTimezone=UTC"
flywayUser := "rey"
flywayPassword := "rey"