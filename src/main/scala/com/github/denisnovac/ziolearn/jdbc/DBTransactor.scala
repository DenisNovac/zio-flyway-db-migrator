package com.github.denisnovac.ziolearn.jdbc

import zio.*
import zio.interop.catz.*
import com.github.denisnovac.ziolearn.config.DBConfig
import com.github.denisnovac.ziolearn.cats.ZioCatsLayer.ZioCats
import doobie.hikari.HikariTransactor
import doobie.*
import cats.effect.kernel.Async
import cats.effect.std.Dispatcher
import cats.effect.kernel.Resource
import doobie.util.transactor.Transactor

private[jdbc] object DBTransactor {

  /** Plain Cats Effect transactor */
  private def transactorResource[F[_]: Async](config: DBConfig): Resource[F, HikariTransactor[F]] =
    for {
      ce <- ExecutionContexts.fixedThreadPool[F](config.threads)
      xa <- HikariTransactor.newHikariTransactor[F](
              config.driver,
              config.url,
              config.user,
              config.password,
              ce
            )
    } yield xa

  /** Makes a wrapper over Cats Effect transactor */
  private[jdbc] def make: ZIO[DBConfig & ZioCats[Task] & Scope, Throwable, Transactor[zio.Task]] =
    for {
      config                   <- ZIO.service[DBConfig]
      (given Async[Task])      <- ZIO.service[Async[Task]]
      (given Dispatcher[Task]) <- ZIO.service[Dispatcher[Task]]
      managedTransactor        <- transactorResource[Task](config).toScopedZIO
    } yield managedTransactor
}
