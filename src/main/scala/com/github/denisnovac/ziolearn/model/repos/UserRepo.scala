package com.github.denisnovac.ziolearn.model.repos

import zio.*
import com.github.denisnovac.ziolearn.model.User
import doobie.ConnectionIO
import doobie.*
import doobie.implicits.*
import doobie.implicits.legacy.instant.* // Doobie's Instant codec for SQL

trait UserRepo[F[_]] {
  def read(id: Int): F[Option[User]]
  def lookup(uKey: String): F[Option[User]]
  def upsert(user: User): F[User]
  def delete(id: Int): F[Unit]
}

object UserRepo {
  def make: ZIO[LogHandler, Nothing, UserRepo[ConnectionIO]] =
    for {
      logHandler <- ZIO.service[LogHandler]
    } yield new UserRepo[ConnectionIO] {
      override def read(id: Int): ConnectionIO[Option[User]] =
        sql"SELECT * FROM users WHERE u_id = $id"
          .queryWithLogHandler[User](logHandler)
          .option

      override def lookup(uKey: String): ConnectionIO[Option[User]] =
        sql"SELECT * FROM users WHERE u_key = $uKey"
          .queryWithLogHandler[User](logHandler)
          .option

      override def upsert(user: User): ConnectionIO[User] =
        sql"""INSERT INTO users VALUES (
      ${user.uId}, ${user.uKey}, ${user.uValue}, ${user.createdAt}, ${user.updatedAt}
    ) ON CONFLICT(u_id) DO UPDATE SET
      u_key = ${user.uKey},
      u_value = ${user.uValue},
      updated_at = ${user.updatedAt}
    """.updateWithLogHandler(logHandler)
          .run
          .map(_ => user)

      override def delete(id: Int): doobie.ConnectionIO[Unit] =
        sql"DELETE FROM users WHERE u_id = $id"
          .updateWithLogHandler(logHandler)
          .run
          .map(_ => ())

    }
}
