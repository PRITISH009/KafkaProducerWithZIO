import Dependencies._

scalaVersion := "2.13.8"
version := "1.0.0"
name := "KafkaProducerWithZIO"

libraryDependencies ++= zioLibs ++ logLibs ++ configLibs