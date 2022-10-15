package com.github.denisnovac.ziolearn.jdbc

import zio.*
import zio.logging.*
import doobie.util.transactor.Transactor
import com.github.denisnovac.ziolearn.config.DBConfig
import com.github.denisnovac.ziolearn.cats.ZioCatsLayer.ZioCats

object DBLayer {

  /** Makes migrations and then connection pool
    *
    * @return
    *   doobie transactor layer for injection
    */
  def make: ZLayer[DBConfig & ZioCats[Task], Throwable, Transactor[Task]] =
    ZLayer.fromZIO {
      for {
        _ <- DBMigrator.migrate
        t <- ZIO.scoped(DBTransactor.make)
        _ <- ZIO.logInfo("Migrations and database transactor creation were successful")
      } yield t
    }

}
