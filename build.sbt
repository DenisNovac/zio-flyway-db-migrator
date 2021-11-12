name := "zio-flyway-db-migrator"

version := "0.1"

scalaVersion := "3.1.0"

libraryDependencies += "org.postgresql" % "postgresql"  % "42.3.1"
libraryDependencies += "org.flywaydb"   % "flyway-core" % "8.0.4"

libraryDependencies += "dev.zio" %% "zio" % "1.0.12"

val zioConfig = Seq(
  "dev.zio" %% "zio-config",
  "dev.zio" %% "zio-config-typesafe",
  "dev.zio" %% "zio-config-magnolia"
).map(_ % "1.0.10")

libraryDependencies ++= zioConfig

val zioLogging = Seq(
  "dev.zio" %% "zio-logging",
  "dev.zio" %% "zio-logging-slf4j"
).map(_ % "0.5.13")

libraryDependencies ++= zioLogging
