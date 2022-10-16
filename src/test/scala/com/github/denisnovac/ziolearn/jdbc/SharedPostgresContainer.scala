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

abstract class SharedPostgresContainer extends ZIOSpec[Async[Task] & Transactor[Task] & LogHandler] {

  private val container: ZLayer[Any, Nothing, PostgreSQLContainer[Nothing]] =
    ZLayer.scoped(
      ZIO.acquireRelease(
        ZIO.debug("Opening Postgresql container") *>
          ZIO.succeed(new PostgreSQLContainer("postgres:alpine")).map { c =>
            c.start()
            c
          }
      )(container =>
        ZIO.succeed(container.stop) *>
          ZIO.debug("Container stopped")
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

  override val bootstrap: ZLayer[Scope, Any, Environment] =
    container >+> configLayer >+> asyncLayer >+> LoggerLayer.make(Runtime.default) >+> DBLayer.make

}
