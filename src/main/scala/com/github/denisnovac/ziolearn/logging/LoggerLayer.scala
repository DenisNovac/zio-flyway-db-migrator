package com.github.denisnovac.ziolearn.logging

import zio.*
import zio.logging.LogFormat.*
import zio.logging.backend.SLF4J
import doobie.util.log.LogHandler

object LoggerLayer {

  /** Copypasterd from doobie's default LogHandler.jdkLogHandler
    */
  private def doobieLogger(zioRuntime: zio.Runtime[Any]): LogHandler = LogHandler { event =>
    zio.Unsafe.unsafe { implicit u =>
      zioRuntime.unsafe
        .run {
          event match {
            case doobie.util.log.Success(s, a, e1, e2) =>
              ZIO.logDebug(s"""Successful Statement Execution:
                              |
                              |  ${s.linesIterator.dropWhile(_.trim.isEmpty).mkString("\n  ")}
                              |
                              | arguments = [${a.mkString(", ")}]
                              |   elapsed = ${e1.toMillis.toString} ms exec + ${e2.toMillis.toString} ms processing (${(e1 + e2).toMillis.toString} ms total)
          """.stripMargin)

            case doobie.util.log.ProcessingFailure(s, a, e1, e2, t) =>
              ZIO.logError(s"""Failed Resultset Processing:
                              |
                              |  ${s.linesIterator.dropWhile(_.trim.isEmpty).mkString("\n  ")}
                              |
                              | arguments = [${a.mkString(", ")}]
                              |   elapsed = ${e1.toMillis.toString} ms exec + ${e2.toMillis.toString} ms processing (failed) (${(e1 + e2).toMillis.toString} ms total)
                              |   failure = ${t.getMessage}
          """.stripMargin)

            case doobie.util.log.ExecFailure(s, a, e1, t) =>
              ZIO.logError(s"""Failed Statement Execution:
                              |
                              |  ${s.linesIterator.dropWhile(_.trim.isEmpty).mkString("\n  ")}
                              |
                              | arguments = [${a.mkString(", ")}]
                              |   elapsed = ${e1.toMillis.toString} ms exec (failed)
                              |   failure = ${t.getMessage}
          """.stripMargin)
          }
        }
        .getOrThrowFiberFailure()
    }
  }

  def make(implicit zioRuntime: zio.Runtime[Any]): ZLayer[Any, Nothing, LogHandler] =
    zio.Runtime.removeDefaultLoggers >>> SLF4J.slf4j >>> ZLayer.succeed(doobieLogger(zioRuntime))
}
