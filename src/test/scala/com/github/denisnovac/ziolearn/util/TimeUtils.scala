package com.github.denisnovac.ziolearn.util

import java.time.Instant

trait TimeUtils {

  def timeNow() = {
    val i = Instant.now()
    i.minusNanos(i.getNano())
  }
}
