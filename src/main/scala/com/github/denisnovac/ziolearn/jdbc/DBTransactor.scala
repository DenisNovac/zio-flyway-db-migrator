package com.github.denisnovac.ziolearn.jdbc

import zio.*
import zio.interop.catz.*
import com.github.denisnovac.ziloearn.model.DBConfig
import doobie.hikari.HikariTransactor
import cats.effect.kernel.Sync
import doobie.*
import doobie.implicits.*
import doobie.hikari.*
import cats.implicits.*
import cats.effect.kernel.Async
import cats.effect.std.Dispatcher
import cats.effect.kernel.Resource
import zio.interop.CatsZioInstances
import zio.clock.Clock
import zio.blocking.Blocking
import doobie.util.transactor.Transactor

object DBTransactor extends CatsZioInstances {

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
  def apply: ZLayer[Has[DBConfig] & Clock & Blocking, Throwable, Has[Transactor[Task]]] =
    ZLayer.fromManaged {

      for {
        config                   <- ZIO.accessM[Has[DBConfig]](c => ZIO(c.get)).toManaged_
        (given Runtime[Clock & Blocking]) <- ZIO.runtime[Clock & Blocking].toManaged_ // contains instance of Async[Task]

        // this dispatcher has no finalizer so it should not be used
        (dispatcher, dispatcherStopJob) <- Dispatcher[Task].allocated.toManaged_ // you need a dispatcher to make a ZManaged so workaround here

        // this dispatcher is ZManaged and will be released
        (given Dispatcher[Task]) <- ZManaged.make(ZIO(dispatcher))(release => dispatcherStopJob.mapError(e => throw e))

        managedTransactor <- transactorResource[Task](config).toManaged
      } yield managedTransactor

    }

}
