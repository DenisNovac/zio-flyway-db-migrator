package com.github.denisnovac.ziolearn.jdbc

import zio.*
import zio.logging.*
import org.flywaydb.core.Flyway
import org.flywaydb.core.api.configuration.FluentConfiguration
import scala.jdk.CollectionConverters.*
import scala.annotation.migration
import org.flywaydb.core.api.Location
import org.flywaydb.core.api.MigrationState
import com.github.denisnovac.ziolearn.config.DBConfig

private[jdbc] object DBMigrator {

  private def logValidationErrorsIfAny(flywayConfig: FluentConfiguration): ZIO[Any, Throwable, Unit] =
    for {
      validated <- ZIO.succeed(
                     flywayConfig
                       .ignoreMigrationPatterns("*:pending")
                       .load()
                       .validateWithResult
                   )
      _         <- ZIO.when(!validated.validationSuccessful)(
                     // cats traverse analog for ZIO is foreach
                     ZIO.foreach(validated.invalidMigrations.asScala)(error => ZIO.logError(s"Invalid migration: $error"))
                   )
      _         <- ZIO.when(!validated.validationSuccessful)(
                     ZIO.fail(new Error("Migrations validation failed (see the logs)"))
                   )
    } yield ()

  private def migrationEffect(config: DBConfig): ZIO[Any, Throwable, Int] =
    for {

      flywayConfig <- ZIO.succeed(
                        Flyway.configure
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
                      )

      _ <- logValidationErrorsIfAny(flywayConfig)
      _ <- ZIO.logInfo("Migrations validation successful")

      count <- ZIO.succeed(flywayConfig.load().migrate().migrationsExecuted)

      // fail for any statuses except success (in case of missing migration files, etc)
      _ <- ZIO.foreach(flywayConfig.load().info().all().toList) { i =>
             i.getState match {
               case MigrationState.SUCCESS => ZIO.unit
               case e                      => ZIO.fail(new Error(s"Migration ${i.getDescription} status is not \"SUCCESS\": ${e.toString}"))
             }
           }

    } yield count

  private[jdbc] def migrate: ZIO[DBConfig, Throwable, Unit] =
    for {
      config <- ZIO.service[DBConfig]
      _      <- ZIO.logInfo(s"Starting the migration for host: ${config.url}")
      count  <- migrationEffect(config)

      // _ <- ZIO.when(count < 1)(ZIO.fail(new Error("No migrations were executed")))
      _ <- ZIO.logInfo(s"Successful migrations: $count")
    } yield ()

}
