package com.github.denisnovac.ziolearn

import zio._
import com.github.denisnovac.ziolearn.logging.AppLogger
import zio.config.ZConfig
import com.github.denisnovac.ziolearn.config.AppConfig
import zio.config.ConfigDescriptor
import zio.config.ReadError
import java.io.File
import zio.config.typesafe._

object Main extends ZIOAppDefault {

  private val configLayer: Layer[ReadError[String], AppConfig] =
    ZConfig.fromHoconFile(new File(Files.appConfig), implicitly[ConfigDescriptor[AppConfig]])

  private def program: ZIO[AppConfig, Nothing, Unit] =
    for {
      appConfig <- ZIO.service[AppConfig]
      _         <- ZIO.logInfo("Hello world")
      _         <- ZIO.logInfo(appConfig.toString)
    } yield ()

  override def run =
    program
      .provide(
        AppLogger.logger ++
          configLayer
      )

}

object Files {
  val appConfig = "src/main/resources/application.conf"
}
