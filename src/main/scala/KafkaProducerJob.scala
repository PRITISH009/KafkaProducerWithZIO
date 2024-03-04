import utils.ApplicationLogger
import zio.{RIO, Scope, ZIO, ZIOAppArgs, ZIOAppDefault, ZLayer}

object KafkaProducerJob extends ZIOAppDefault with ApplicationLogger {
  override val bootstrap: ZLayer[ZIOAppArgs, Any, Any] = zioSlf4jLogger

  override def run: RIO[ZIOAppArgs, Unit] = for {
    _ <- ZIO.logInfo("Starting Producer Job")

    _ <- ZIO.logInfo("Ending Producer Job")
  } yield ()
}
