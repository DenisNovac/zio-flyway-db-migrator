package com.github.denisnovac.ziolearn.config

import zio.config.magnolia.descriptor
import zio.config.ConfigDescriptor

case class AppConfig(
    dbConfig: DBConfig
)

object AppConfig {
  given ConfigDescriptor[AppConfig] = descriptor[AppConfig]
}
