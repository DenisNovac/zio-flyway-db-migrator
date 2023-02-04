import sbt._

object Dependencies {

  object Versions {
    val cats       = "2.8.0"
    val catsEffect = "3.4-148221d"

    val doobie = "1.0.0-RC2"

    val flyway = "9.4.0"

    val log4j   = "2.19.0"
    val logback = "1.4.4"

    val postgresql = "42.5.0"

    val zio        = "2.0.1"
    val zioConfig  = "3.0.2"
    val zioLogging = "2.1.2"
    val zioInterop = "3.3.0+12-687b46a7-SNAPSHOT"

    val scalatest      = "3.2.14"
    val testcontainers = "1.17.5"
    val weaver         = "0.8.0"
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

  val testing = Seq(
    "org.scalatest"     %% "scalatest"      % Versions.scalatest      % Test,
    "org.testcontainers" % "testcontainers" % Versions.testcontainers % Test,
    "org.testcontainers" % "postgresql"     % Versions.testcontainers % Test,
    "dev.zio"           %% "zio-test"       % Versions.zio            % Test,
    "dev.zio"           %% "zio-test-sbt"   % Versions.zio            % Test
  )
}
