package com.github.denisnovac.ziolearn.model

import java.time.Instant

case class User(
    uId: Int,
    uKey: String,
    uValue: String,
    createdAt: Instant,
    updatedAt: Instant
)
