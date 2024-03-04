package utils

import org.slf4j.{Logger, LoggerFactory}
import zio.{Runtime, ZIOAppArgs, ZLayer}
import zio.logging.backend.SLF4J

trait ApplicationLogger {

  protected lazy val logger: Logger                          = LoggerFactory.getLogger(getClass.getName)
  protected val zioSlf4jLogger: ZLayer[ZIOAppArgs, Any, Any] = Runtime.removeDefaultLoggers >>> SLF4J.slf4j
}
