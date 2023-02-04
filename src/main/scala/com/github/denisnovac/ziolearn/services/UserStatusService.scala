package com.github.denisnovac.ziolearn.services

import com.github.denisnovac.ziolearn.model.Status
import com.github.denisnovac.ziolearn.model.repos.{StatusRepo, UserRepo}
import doobie.ConnectionIO
import doobie.implicits.*
import doobie.util.transactor.Transactor
import cats.effect.Sync
import cats.implicits.*
import zio.*
import UserStatusService.*
import UserStatusService.UserStatusServiceErrors.*
import scala.util.control.NoStackTrace
import com.github.denisnovac.ziolearn.cats.ZioCatsLayer.ZioCats
import cats.effect.kernel.Async

trait UserStatusService {
  def insert(status: Status): ZIO[Any, UserStatusServiceErrors, Status]
  def update(status: Status): ZIO[Any, UserStatusServiceErrors, Status]
  def remove(id: Int): ZIO[Any, UserStatusServiceErrors, Unit]
}

object UserStatusService {
  sealed trait UserStatusServiceErrors extends NoStackTrace
  object UserStatusServiceErrors {
    case class NoUserFound(userId: Int)     extends UserStatusServiceErrors
    case class NoStatusFound(statusId: Int) extends UserStatusServiceErrors
    case class NoStatusDefined(userId: Int) extends UserStatusServiceErrors

  }

  def make: ZIO[UserRepo[
    ConnectionIO
  ] & StatusRepo[ConnectionIO] & Transactor[Task] & ZioCats[Task], Nothing, UserStatusService] =
    for {
      userRepo   <- ZIO.service[UserRepo[ConnectionIO]]
      statusRepo <- ZIO.service[StatusRepo[ConnectionIO]]
      xa         <- ZIO.service[Transactor[Task]]

      (given Async[Task]) <- ZIO.service[Async[Task]]
    } yield new UserStatusService {

      override def insert(status: Status): ZIO[Any, UserStatusServiceErrors, Status] = {
        for {
          maybeUser <- userRepo.read(status.uId)
          _         <- Sync[ConnectionIO].fromOption(maybeUser, NoUserFound(status.uId))
          result    <- statusRepo.upsert(status)
        } yield result
      }.transact(xa)
        .refineToOrDie[UserStatusServiceErrors]

      override def update(status: Status): ZIO[Any, UserStatusServiceErrors, Status] = {
        for {
          maybeOldStatus <- statusRepo.read(status.uId)
          _              <- Sync[ConnectionIO].fromOption(maybeOldStatus, NoStatusDefined(status.uId))
          result         <- statusRepo.upsert(status)
        } yield result
      }.transact(xa)
        .refineToOrDie[UserStatusServiceErrors]

      override def remove(id: Int): ZIO[Any, UserStatusServiceErrors, Unit] = {
        for {
          maybeOldStatus <- statusRepo.read(id)
          _              <- Sync[ConnectionIO].fromOption(maybeOldStatus, NoStatusFound(id))
          _              <- statusRepo.delete(id)
        } yield ()
      }.transact(xa)
        .refineToOrDie[UserStatusServiceErrors]
    }

}
