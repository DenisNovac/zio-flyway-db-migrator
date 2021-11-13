import sbt._

object Dependencies {

  object Versions {
    val cats       = "2.6.1"
    val catsEffect = "3.2.9"

    val doobie = "1.0.0-RC1"

    val flyway = "8.0.4"

    val log4j   = "2.14.1"
    val logback = "1.2.7"

    val postgresql = "42.3.1"

    val zio        = "1.0.12"
    val zioConfig  = "1.0.10"
    val zioLogging = "0.5.13"
    val zioInterop = "3.1.1.0"
  }

  val cats       = "org.typelevel" %% "cats-core"   % Versions.cats
  val catsEffect = "org.typelevel" %% "cats-effect" % Versions.catsEffect

  val doobie = Seq(
    "org.tpolecat" %% "doobie-core",
    "org.tpolecat" %% "doobie-hikari",
    "org.tpolecat" %% "doobie-postgres",
    "org.tpolecat" %% "doobie-h2"
  ).map(_ % Versions.doobie)

  val flyway = Seq(
    "org.flywaydb" % "flyway-core",
    "org.flywaydb" % "flyway-maven-plugin"
  ).map(_ % Versions.flyway)

  val log4j = "org.apache.logging.log4j" % "log4j-core" % Versions.log4j

  val logback = "ch.qos.logback" % "logback-classic" % Versions.logback

  val postgresqlDriver = "org.postgresql" % "postgresql" % Versions.postgresql

  val zio = "dev.zio" %% "zio" % Versions.zio

  val zioConfig = Seq(
    "dev.zio" %% "zio-config",
    "dev.zio" %% "zio-config-typesafe",
    "dev.zio" %% "zio-config-magnolia"
  ).map(_ % Versions.zioConfig)

  val zioLogging = Seq(
    "dev.zio" %% "zio-logging",
    "dev.zio" %% "zio-logging-slf4j"
  ).map(_ % Versions.zioLogging)

  val zioInterop = "dev.zio" %% "zio-interop-cats" % Versions.zioInterop

}
