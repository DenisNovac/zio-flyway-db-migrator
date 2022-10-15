package com.github.denisnovac.ziolearn.model.repos

import com.github.denisnovac.ziolearn.model.Status
import doobie.*
import doobie.implicits.*
import doobie.implicits.legacy.instant.* // Doobie's Instant codec for SQL

trait StatusRepo[F[_]] {
  def read(id: Int): F[Option[Status]]
  def upsert(status: Status): F[Status]
  def delete(id: Int): F[Unit]
}

object StatusRepo {
  def make = new StatusRepo[ConnectionIO] {
    override def read(id: Int): ConnectionIO[Option[Status]] =
      sql"SELECT * FROM statuses WHERE u_id = $id"
        .queryWithLogHandler[Status](LogHandler.jdkLogHandler)
        .option

    override def upsert(status: Status): ConnectionIO[Status] =
      sql"""INSERT INTO statuses VALUES (
      ${status.uId}, ${status.uStatus}, ${status.updatedAt}
    ) ON CONFLICT UPDATE
    """.updateWithLogHandler(LogHandler.jdkLogHandler).run.map(_ => status)

    override def delete(id: Int): doobie.ConnectionIO[Unit] =
      sql"DELETE FROM statuses WHERE u_id = $id".updateWithLogHandler(LogHandler.jdkLogHandler).run.map(_ => ())

  }
}
