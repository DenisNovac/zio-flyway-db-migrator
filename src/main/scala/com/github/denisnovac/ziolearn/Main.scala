package com.github.denisnovac.ziolearn

import zio.*
import zio.interop.catz.*
import zio.config.typesafe.*
import zio.config.ZConfig
import zio.config.ConfigDescriptor
import zio.config.ReadError
import com.github.denisnovac.ziolearn.logging.LoggerLayer
import com.github.denisnovac.ziolearn.config.AppConfig
import com.github.denisnovac.ziolearn.jdbc.DBLayer

import java.io.File
import doobie.util.transactor.Transactor
import com.github.denisnovac.ziolearn.cats.ZioCatsLayer
import com.github.denisnovac.ziolearn.config.DBConfig

object Main extends zio.interop.catz.CatsApp {

  private val configsLayer: ZLayer[Any, ReadError[String], DBConfig] =
    ZLayer {
      {
        for {
          appConfig <- ZIO.service[AppConfig]
        } yield appConfig.dbConfig
      }.provide(
        ZConfig
          .fromHoconFile(new File(Files.appConfig), implicitly[ConfigDescriptor[AppConfig]])
      )
    }

  private def program: ZIO[Any, Nothing, Unit] =
    for {
      _ <- ZIO.logInfo("Hello world")
    } yield ()

  override def run =
    program
      .provideLayer(
        ZioCatsLayer.make >+>
          LoggerLayer.layer >+>
          configsLayer >+>
          DBLayer.make
      )
      .orDie
      .exitCode

}

object Files {
  val appConfig = "src/main/resources/application.conf"
}
