package com.github.denisnovac.ziolearn.cats

import cats.effect.kernel.Async
import cats.effect.std.Dispatcher
import zio.*
import zio.interop.catz.*

object ZioCatsLayer {
  type ZioCats[F[_]] = Async[F] & Dispatcher[F]

  private def dispatcher(zioRuntime: zio.Runtime[Any]) = {

    val (disp, close) = Unsafe.unsafe { implicit unsafe =>
      zioRuntime.unsafe
        .run(
          cats.effect.std.Dispatcher.parallel[zio.Task].allocated
        )
        .getOrThrowFiberFailure()
    }

    ZIO.acquireRelease(ZIO.succeed(disp))(_ => close.orDie)
  }

  def make(implicit zioRuntime: zio.Runtime[Any]): ZLayer[Any, Nothing, ZioCats[Task]] =
    ZLayer.succeed(Async[Task]) ++ ZLayer.scoped(dispatcher(zioRuntime))
}
