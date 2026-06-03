addSbtPlugin("org.playframework" % "sbt-plugin" % "3.0.11")
addSbtPlugin("org.foundweekends.giter8" % "sbt-giter8-scaffold" % "0.18.0")
addSbtPlugin("com.github.sbt" % "flyway-sbt" % "10.21.0")

libraryDependencies ++= Seq(
  "org.flywaydb" % "flyway-mysql" % "10.21.0",
  "com.mysql"    % "mysql-connector-j"     % "9.0.0"
)

