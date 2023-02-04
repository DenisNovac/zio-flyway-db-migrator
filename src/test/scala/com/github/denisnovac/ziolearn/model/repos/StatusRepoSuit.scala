package com.github.denisnovac.ziolearn.model.repos

import com.github.denisnovac.ziolearn.jdbc.SharedPostgresContainer
import doobie.util.transactor.Transactor
import zio.Scope
import zio.test.Spec
import zio.Task
import zio.*
import zio.test.*
import com.github.denisnovac.ziolearn.model.*
import doobie.implicits.*
import cats.effect.kernel.Async
import doobie.util.log.LogHandler
import doobie.ConnectionIO

import com.github.denisnovac.ziolearn.util.TimeUtils

object StatusRepoSuit extends SharedPostgresContainer with TimeUtils {

  override def spec: Spec[Transactor[Task] & Async[Task] & LogHandler & (TestEnvironment & Scope), Any] =
    test("CRUD for StatusRepo") {

      val expectedStatus = Status(100, "StatusRepoSuitTest", timeNow())
      val modifiedStatus = Status(100, "StatusRepoSuitTest2", timeNow())

      def crud(repo: StatusRepo[ConnectionIO], urepo: UserRepo[ConnectionIO]) = for {
        _ <- urepo.upsert(User(100, "test", "test", timeNow(), timeNow()))

        e  <- repo.upsert(expectedStatus)
        er <- repo.read(100)

        m  <- repo.upsert(modifiedStatus)
        mr <- repo.read(100)

        _ <- repo.delete(100)
        n <- repo.read(100)

      } yield assertTrue(e == expectedStatus) &&
        assertTrue(er.contains(expectedStatus)) &&
        assertTrue(m == modifiedStatus) &&
        assertTrue(mr.contains(modifiedStatus)) &&
        assertTrue(n.isEmpty)

      for {
        (given Async[Task]) <- ZIO.service[Async[Task]]
        xa                  <- ZIO.service[Transactor[Task]]
        repo                <- StatusRepo.make
        urepo               <- UserRepo.make
        r                   <- crud(repo, urepo).transact(xa)
      } yield r
    }

}
