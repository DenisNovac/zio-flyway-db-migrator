name := "zio-flyway-db-migrator"

version := "0.1"

scalaVersion := "3.1.0"

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

scalacOptions ++= Seq(
  "-source:future"
)
