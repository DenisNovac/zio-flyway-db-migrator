package com.github.denisnovac.jdbc

import zio._
import zio.console._
import zio.config.typesafe.TypesafeConfigSource
import zio.config.ConfigDescriptor
import scala.io.Source
import zio.config._
import zio.logging._
import zio.clock.Clock

object Main extends zio.App {

  /** Layer for logging */
  private val loggingLayer: ZLayer[Console & Clock, Nothing, Has[Logger[String]]] =
    Logging.console(
      logLevel = LogLevel.Info,
      format = LogFormat.ColoredLogFormat()
    ) >>> Logging.withRootLoggerName("zio-flyway-db-migrator")

  /** A Layer without environment which can throw an error and returns the config */
  private val configLayer: Layer[ReadError[_], Has[DBConfig]] =
    ZIO
      .fromEither(
        TypesafeConfigSource
          .fromHoconFile(new java.io.File("src/main/resources/application.conf"))
          .flatMap(source => read[DBConfig](implicitly[ConfigDescriptor[DBConfig]].from(source)))
      )
      .toLayer

  /** Actual program with requirements */
  private def program: ZIO[Has[DBConfig] with Has[Logger[String]], Throwable, Unit] =
    for {
      config <- ZIO.accessM[Has[DBConfig]](c => ZIO.succeed(c.get))
      _      <- DBMigrator.migrate
    } yield ()

  /** Using the default run method just to add layers to the actual program */
  def run(args: List[String]): ZIO[ZEnv, Nothing, ExitCode] =
    program
      .provideLayer(
        loggingLayer ++ configLayer ++ Console.live
      )
      .mapError[Nothing](throwable => throw throwable)
      .map(_ => ExitCode.success)
}
