package com.github.denisnovac.ziolearn.logging

import zio.*
import zio.logging.LogFormat.*
import zio.logging.backend.SLF4J

object LoggerLayer {
  private val format = timestamp.fixed(32) |-| level |-| label("message", quoted(line))

  val layer: ZLayer[Any, Nothing, Unit] = zio.Runtime.removeDefaultLoggers >>> SLF4J.slf4j
}
