package com.github.denisnovac.jdbc

import zio._
import zio.logging._
import org.flywaydb.core.Flyway
import org.flywaydb.core.api.configuration.FluentConfiguration
import scala.jdk.CollectionConverters._
import scala.annotation.migration
import org.flywaydb.core.api.Location

object DBMigrator {

  def migrate: ZIO[Has[DBConfig] with Has[Logger[String]], Throwable, Unit] =
    for {
      config <- ZIO.accessM[Has[DBConfig]](c => ZIO.succeed(c.get))
      _      <- log.info(s"Starting the migration for host: ${config.url}")
      count  <- migrationEffect
      _      <- log.info(s"Successful migrations: $count")
    } yield ()

  private def migrationEffect: ZIO[Has[DBConfig] with Has[Logger[String]], Throwable, Int] =
    for {
      config <- ZIO.accessM[Has[DBConfig]](c => ZIO.succeed(c.get))

      flywayConfig = Flyway.configure
                       .dataSource(
                         config.url,
                         config.user,
                         config.password
                       )
                       .group(true)
                       .outOfOrder(false)
                       .locations("classpath:migrations")
                       .baselineOnMigrate(true)

      _ <- logValidationErrorsIfAny(flywayConfig)

      count <- ZIO(flywayConfig.load().migrate().migrationsExecuted)

    } yield count

  private def logValidationErrorsIfAny(flywayConfig: FluentConfiguration): ZIO[Has[Logger[String]], Throwable, Unit] =
    for {
      validated <- ZIO(
                     flywayConfig
                       .ignoreMigrationPatterns("*:pending")
                       .load()
                       .validateWithResult
                   )
      _         <- ZIO.when(!validated.validationSuccessful)(
                     // cats traverse analog for ZIO is foreach
                     ZIO.foreach(validated.invalidMigrations.asScala)(error => log.error(s"Invalid migration: $error"))
                   )
    } yield ()

}
