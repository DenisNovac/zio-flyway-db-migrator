package com.github.denisnovac.jdbc

import zio._
import zio.logging._
import org.flywaydb.core.Flyway
import org.flywaydb.core.api.configuration.FluentConfiguration
import scala.jdk.CollectionConverters._
import scala.annotation.migration
import org.flywaydb.core.api.Location
import org.flywaydb.core.api.MigrationState

object DBMigrator {

  def migrate: ZIO[Has[DBConfig] with Has[Logger[String]], Throwable, Unit] =
    for {
      config <- ZIO.accessM[Has[DBConfig]](c => ZIO.succeed(c.get))
      _      <- log.info(s"Starting the migration for host: ${config.url}")
      count  <- migrationEffect

      // _ <- ZIO.when(count < 1)(ZIO.fail(new Error("No migrations were executed")))
      _ <- log.info(s"Successful migrations: $count")
    } yield ()

  private def migrationEffect: ZIO[Has[DBConfig] with Has[Logger[String]], Throwable, Int] =
    for {
      config <- ZIO.accessM[Has[DBConfig]](c => ZIO.succeed(c.get))

      flywayConfig = Flyway.configure
                       .loggers("log4j2")
                       .dataSource(
                         config.url,
                         config.user,
                         config.password
                       )
                       .group(true)
                       .outOfOrder(false)
                       .locations(config.migrationsLocation)
                       .failOnMissingLocations(true)
                       .baselineOnMigrate(true)

      _ <- logValidationErrorsIfAny(flywayConfig)
      _ <- log.info("Migrations validation successful")

      count <- ZIO(flywayConfig.load().migrate().migrationsExecuted)

      // fail for any statuses except success (in case of missing migration files, etc)
      _ <- ZIO.foreach(flywayConfig.load().info().all().toList) { i =>
             i.getState match {
               case MigrationState.SUCCESS => ZIO.unit
               case e                      => ZIO.fail(new Error(s"Migration ${i.getDescription} status is not \"SUCCESS\": ${e.toString}"))
             }
           }

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
      _         <- ZIO.when(!validated.validationSuccessful)(
                     ZIO.fail(new Error("Migrations validation failed (see the logs)"))
                   )
    } yield ()

}
