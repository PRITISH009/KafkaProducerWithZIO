import com.typesafe.config.{Config, ConfigFactory, ConfigValueFactory}
import org.apache.kafka.clients.producer.ProducerRecord
import utils.ApplicationLogger
import zio.kafka.producer.{Producer, ProducerSettings}
import zio.{RIO, Task, ZIO, ZIOAppArgs, ZIOAppDefault, ZLayer}

object KafkaProducerJob extends ZIOAppDefault with ApplicationLogger {
  override val bootstrap: ZLayer[ZIOAppArgs, Any, Any] = zioSlf4jLogger

  val producerSettings: ProducerSettings = ProducerSettings(List("kafka-service:9092"))
    .withProperty("auto.offset.reset", "latest")
    .withProperty("security.protocol", "SSL")
    .withProperty("ssl.endpoint.identification.algorithm", "")

  private val producerLayer: ZLayer[Any, Throwable, Producer] =
    ZLayer.scoped(
      Producer.make(
        producerSettings
      )
    )

  case class Arg(key: String, value: String)

  def extractArg(arg: String): Arg = {
    val pattern = "-D(.*)=(.*)".r
    arg match {
      case pattern(key, value) => Arg(key, value)
    }
  }

  def produceRandomRecord(config: Config): ProducerRecord[Int, String] = {
    val topicName: String = config.getString("kafkaTopic")
    new ProducerRecord(topicName, 1, "")
  }

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
    _       <- ZIO.logInfo("Starting Producer Job")
    args    <- readAllArguments()
    config  <- createConfig(args)
    _       <- ZIO.logInfo(s"Got Topic Name - ${config.getString("topic")}")
    _       <- ZIO.logInfo("Ending Producer Job")
  } yield ()
}
