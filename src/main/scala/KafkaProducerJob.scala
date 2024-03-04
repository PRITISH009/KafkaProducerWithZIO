import com.typesafe.config.{Config, ConfigFactory, ConfigValueFactory}
import utils.ApplicationLogger
import utils.ConfigUtils.{Arg, extractArg}
import utils.ProducerUtils.{getProducerLayer, getProducerRecord, getProducerSettings}
import zio.kafka.producer.Producer
import zio.kafka.serde.Serde
import zio.{RIO, Task, ZIO, ZIOAppArgs, ZIOAppDefault, ZLayer}

object KafkaProducerJob extends ZIOAppDefault with ApplicationLogger {
  override val bootstrap: ZLayer[ZIOAppArgs, Any, Any] = zioSlf4jLogger

  def readAllArguments(): RIO[ZIOAppArgs, List[Arg]] = for {
    _ <- ZIO.logInfo("Reading Program Arguments")
    extractedProgramArgs <- ZIOAppArgs.getArgs.map(_.toList)

    programArgs <- ZIO.attempt(extractedProgramArgs.map(extractArg))
    _ <- ZIO.logInfo(s"Got Program Arguments - [${programArgs.mkString(",")}]")

    systemArgs <- ZIO.attempt {
      sys.env.map {
        case (key: String, value: String) => Arg(key, value)
      }.toList
    }
    _ <- ZIO.logInfo(s"Got System Arguments - [${systemArgs.mkString(",")}]")

  } yield(programArgs ++ systemArgs)

  def createConfig(args: List[Arg]): Task[Config] = for {
    _ <- ZIO.logInfo("Creating Config")
    config <- ZIO.attempt {
      val emptyConfig: Config = ConfigFactory.empty()
      args.foldLeft(emptyConfig)((currentConfig: Config, nextArg: Arg) => {
        currentConfig.withValue(nextArg.key, ConfigValueFactory.fromAnyRef(nextArg.value))
      })
    }
  } yield(config)

  override def run: RIO[ZIOAppArgs, Unit] = for {
    _ <- ZIO.logInfo("Starting Producer Job")
    args <- readAllArguments()
    config <- createConfig(args)

    _ <- ZIO.logInfo("Creating Producer Settings")
    producerSettings <- ZIO.attempt(getProducerSettings(config))
    _ <- ZIO.logInfo(s"Producer Settings - $producerSettings")

    _ <- ZIO.logInfo("Creating Producer Layer")
    producerLayer <- ZIO.attempt(getProducerLayer(producerSettings))

    _ <- ZIO.logInfo("Creating Producer Record")
    producerRecord <- ZIO.attempt(getProducerRecord(config))
    _ <- ZIO.logInfo(s"Created Producer Record - $producerRecord")

    _ <- ZIO.logInfo("Creating Producer")
    _ <- Producer
      .produce(producerRecord, Serde.int, Serde.string)
      .provideLayer(producerLayer)

    _ <- ZIO.logInfo(s"Ended Producing Record")
    _ <- ZIO.logInfo("Ending Producer Job")
  } yield ()
}
