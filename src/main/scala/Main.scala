import zio._

object Main extends ZIOAppDefault {

  override def run: ZIO[Any & (ZIOAppArgs & Scope), Any, Any] = for {
    _ <- ZIO.debug("Hello world")
  } yield ()

}
