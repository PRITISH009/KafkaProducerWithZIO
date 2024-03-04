import Versions._
import sbt._

object Dependencies {
  lazy val zioLibs: List[ModuleID] = List(
    "dev.zio"                %% "zio"                     % zioVersion,
    "dev.zio"                %% "zio-logging-slf4j"       % zioLogVersion,
    "dev.zio"                %% "zio-json"                % zioJsonVersion,
    "dev.zio"                %% "zio-config-typesafe"     % zioConfigVersion,
    "dev.zio"                %% "zio-kafka"               % zioKafkaVersion,
  )

  lazy val logLibs: List[ModuleID] = List(
    "ch.qos.logback"         % "logback-classic"          % logbackVersion,
  )
}
