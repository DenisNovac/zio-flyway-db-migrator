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

object StatusRepoSuit extends SharedPostgresContainer {

  override def spec: Spec[Transactor[Task] & Async[Task] & (TestEnvironment & Scope), Any] = {

    val repo  = StatusRepo.make
    val urepo = UserRepo.make

    test("CRUD for StatusRepo") {

      val expectedStatus = Status(100, "test", Instant.now())
      val modifiedStatus = Status(100, "test2", Instant.now())

      val crud = for {
        _ <- urepo.upsert(User(100, "test", "test", Instant.now(), Instant.now()))

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
        _                   <- crud.transact(xa)
      } yield assertTrue(true)
    }

  }

}
