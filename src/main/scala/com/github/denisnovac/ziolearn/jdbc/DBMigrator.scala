package com.github.denisnovac.ziolearn.jdbc

import zio.*
import zio.logging.*
import org.flywaydb.core.Flyway
import org.flywaydb.core.api.configuration.FluentConfiguration
import scala.jdk.CollectionConverters.*
import scala.annotation.migration
import org.flywaydb.core.api.Location
import org.flywaydb.core.api.MigrationState

object DBMigrator {}
