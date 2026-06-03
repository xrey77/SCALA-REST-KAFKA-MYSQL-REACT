name := """scala-kafka-rest-mysql-react"""
organization := "com.scala"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.13.18"

libraryDependencies ++= Seq(
  guice,
  "org.scalatestplus.play" %% "scalatestplus-play"   % "7.0.2" % Test,
  "com.mysql"               % "mysql-connector-j"     % "9.1.0",
  "org.flywaydb"            % "flyway-core"           % "10.21.0",
  "org.flywaydb"            % "flyway-mysql" % "10.21.0"
)



enablePlugins(FlywayPlugin)
flywayLocations := Seq("filesystem:src/main/resources/db/migration")
flywayUrl := "jdbc:mysql://127.0.0.1:3306/scala_kafka"
flywayUser := "rey"
flywayPassword := "rey"

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "com.scala.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "com.scala.binders._"
