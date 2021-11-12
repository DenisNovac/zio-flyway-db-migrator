package com.github.denisnovac.jdbc

import zio.config.magnolia.descriptor
import zio.config.ConfigDescriptor

case class DBConfig(url: String, user: String, password: String)

object DBConfig {
  given zio.config.ConfigDescriptor[DBConfig] = descriptor[DBConfig]
}
