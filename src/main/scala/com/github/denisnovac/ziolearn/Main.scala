package com.github.denisnovac.ziolearn

import zio.*
import zio.console.*
import zio.config.typesafe.TypesafeConfigSource
import zio.config.ConfigDescriptor
import scala.io.Source
import zio.config.*
import zio.logging.*
import zio.clock.Clock
import jdbc.DBMigrator
import model.DBConfig
import com.github.denisnovac.ziolearn.jdbc.DBService
import zio.blocking.Blocking
import doobie.util.transactor.Transactor
import doobie.util.pretty.Block

object Main extends zio.App {

  /** Layer for logging */
  private val loggingLayer: ZLayer[Console & Clock, Nothing, Has[Logger[String]]] =
    Logging.console(
      logLevel = LogLevel.Info,
      format = LogFormat.ColoredLogFormat()
    ) >>> Logging.withRootLoggerName("zio-flyway-db-migrator")

  /** A Layer without environment which can throw an error and returns the config */
  private val configLayer: Layer[ReadError[?], Has[DBConfig]] =
    ZIO
      .fromEither(
        TypesafeConfigSource
          .fromHoconFile(new java.io.File("src/main/resources/application.conf"))
          .flatMap(source => read[DBConfig](implicitly[ConfigDescriptor[DBConfig]].from(source)))
      )
      .toLayer

  /** Actual program with requirements */
  private def program: ZIO[Has[Logger[String]], Throwable, Unit] =
    for {
      _ <- log.info("Startup were successful")
    } yield ()

  /** Using the default run method just to add layers to the actual program */
  def run(args: List[String]): ZIO[ZEnv, Nothing, ExitCode] =
    program
      .provideLayer(
        Console.live >+> Clock.live >+> Blocking.live >+> configLayer >+> loggingLayer >+> DBService.databaseLayer
      )
      .mapError[Nothing](throwable => throw throwable)
      .map(_ => ExitCode.success)

  val x = Console.live >+> Clock.live >+> Blocking.live >+> configLayer >+> loggingLayer >+> DBService.databaseLayer

}
