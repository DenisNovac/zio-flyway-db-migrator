package com.github.denisnovac.ziolearn.model.repos

import com.github.denisnovac.ziolearn.jdbc.SharedPostgresContainer
import doobie.util.transactor.Transactor
import zio.Scope
import zio.test.Spec
import zio.Task
import zio.*
import zio.test.*
import com.github.denisnovac.ziolearn.model.*
import java.time.Instant
import doobie.implicits.*
import cats.effect.kernel.Async

object UserRepoSuit extends SharedPostgresContainer {
  override def spec: Spec[Transactor[Task] & Async[Task] & (TestEnvironment & Scope), Any] = {

    val repo = UserRepo.make

    test("CRUD for UserRepo") {

      val expectedUser     = User(1, "UserRepoSuitTest", "test", Instant.now(), Instant.now())
      val modifiedExpected = expectedUser.copy(uValue = "test2")

      val crud = for {
        i <- repo.upsert(expectedUser)
        r <- repo.read(expectedUser.uId)

        m  <- repo.upsert(modifiedExpected)
        ml <- repo.lookup(modifiedExpected.uKey)

        _  <- repo.delete(modifiedExpected.uId)
        d  <- repo.read(modifiedExpected.uId)
        dl <- repo.lookup(modifiedExpected.uKey)

      } yield assertTrue(i == expectedUser) &&
        assertTrue(r.contains(expectedUser)) &&
        assertTrue(m == modifiedExpected) &&
        assertTrue(ml.contains(modifiedExpected)) &&
        assertTrue(d.orElse(dl).isEmpty)

      for {
        (given Async[Task]) <- ZIO.service[Async[Task]]
        xa                  <- ZIO.service[Transactor[Task]]
        r                   <- crud.transact(xa)
      } yield r
    }

  }
}
