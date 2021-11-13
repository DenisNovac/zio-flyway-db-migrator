package com.github.denisnovac.ziloearn.model

import zio.config.magnolia.descriptor
import zio.config.ConfigDescriptor

case class DBConfig(
    driver: String,
    url: String,
    user: String,
    password: String,
    migrationsLocation: String,
    threads: Int
)

object DBConfig {
  given zio.config.ConfigDescriptor[DBConfig] = descriptor[DBConfig]
}
