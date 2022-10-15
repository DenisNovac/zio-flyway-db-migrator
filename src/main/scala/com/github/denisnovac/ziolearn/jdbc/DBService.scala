package com.github.denisnovac.ziolearn.jdbc

import zio.*
import zio.logging.*
import doobie.util.transactor.Transactor
import com.github.denisnovac.ziolearn.model.DBConfig
import zio.logging.Logger
import zio.blocking.Blocking
import zio.clock.Clock

object DBService {

  /** Makes migrations and then connection pool
    *
    * @return
    *   doobie transactor layer for injection
    */
  def databaseLayer: ZLayer[Has[DBConfig] & Has[Logger[String]] & Clock & Blocking, Throwable, Has[Transactor[Task]]] =
    ZLayer.fromManaged {
      for {
        _ <- DBMigrator.migrate.toManaged_
        t <- DBTransactor.make
        _ <- log.info("Migrations and database transactor creation were successful").toManaged_
      } yield t
    }

}
