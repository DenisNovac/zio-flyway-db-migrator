name := "zio-flyway-db-migrator"

version := "0.1"

scalaVersion := "3.2.0"

// for zio-interop
resolvers +=
  "Sonatype OSS Snapshots".at("https://oss.sonatype.org/content/repositories/snapshots")

libraryDependencies += Dependencies.postgresqlDriver
libraryDependencies ++= Dependencies.flyway
libraryDependencies += Dependencies.log4j
libraryDependencies += Dependencies.logback

libraryDependencies += Dependencies.cats
libraryDependencies += Dependencies.catsEffect

libraryDependencies ++= Dependencies.doobie

libraryDependencies += Dependencies.zio
libraryDependencies ++= Dependencies.zioConfig
libraryDependencies ++= Dependencies.zioLogging
libraryDependencies += Dependencies.zioInterop
