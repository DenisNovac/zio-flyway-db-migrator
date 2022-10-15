package com.github.denisnovac.ziolearn.model

import java.time.Instant

case class Status(
    uId: Int,
    uStatus: String,
    updatedAt: Instant
)
