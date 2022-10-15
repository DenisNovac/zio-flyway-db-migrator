package com.github.denisnovac.ziolearn.logging

import zio.*
import zio.logging.LogFormat.*
import zio.logging.backend.SLF4J

object AppLogger {
  private val format = timestamp.fixed(32) |-| level |-| label("message", quoted(line))

  val logger: ZLayer[Any, Nothing, Unit] = zio.Runtime.removeDefaultLoggers >>> SLF4J.slf4j
}
