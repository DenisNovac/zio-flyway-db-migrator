package com.github.denisnovac.ziolearn.jdbc

import zio.*
import zio.interop.catz.*
import com.github.denisnovac.ziolearn.config.DBConfig
import org.testcontainers.containers.PostgreSQLContainer
import doobie.util.transactor.Transactor
import zio.test.*
import com.github.denisnovac.ziolearn.cats.ZioCatsLayer
import com.github.denisnovac.ziolearn.cats.ZioCatsLayer.ZioCats
import cats.effect.kernel.Async
import doobie.util.log.LogHandler
import com.github.denisnovac.ziolearn.logging.LoggerLayer

type SharedPostgresContainerEnvironment = Async[Task] & Transactor[Task] & LogHandler

abstract class SharedPostgresContainer extends ZIOSpec[SharedPostgresContainerEnvironment] {

  private val container: ZLayer[Any, Nothing, PostgreSQLContainer[Nothing]] =
    ZLayer.scoped(
      ZIO.acquireRelease(
        ZIO.succeed(new PostgreSQLContainer("postgres:alpine")).flatMap { container =>
          ZIO.succeed(container.start()) *>
            ZIO.debug(s"Opened PostgreSQL container ${container.getJdbcUrl()}") *>
            ZIO.succeed(container)
        }
      )(container =>
        ZIO.debug(s"Stopping PostgreSQL container ${container.getJdbcUrl()}") *>
          ZIO.succeed(container.stop)
      )
    )

  private val configLayer: ZLayer[PostgreSQLContainer[Nothing], Nothing, DBConfig] =
    ZLayer {
      for {
        c <- ZIO.service[PostgreSQLContainer[Nothing]]
      } yield DBConfig(
        driver = c.getDriverClassName,
        url = c.getJdbcUrl,
        user = c.getUsername,
        password = c.getPassword,
        migrationsLocation = "classpath:flyway",
        threads = 10
      )
    }

  private val asyncLayer: ZLayer[Any, Nothing, ZioCats[Task]] =
    ZioCatsLayer.make(Runtime.default)

  private val loggerLayer: ZLayer[Any, Nothing, LogHandler] =
    LoggerLayer.make(Runtime.default)

  override val bootstrap: ZLayer[Any, Any, SharedPostgresContainerEnvironment] =
    container >+> configLayer >+> asyncLayer >+> loggerLayer >+> DBLayer.make

}
