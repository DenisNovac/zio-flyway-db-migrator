package com.github.denisnovac.ziolearn.config

case class DBConfig(
    driver: String,
    url: String,
    user: String,
    password: String,
    migrationsLocation: String,
    threads: Int
)
