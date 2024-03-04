package utils

import com.typesafe.config.Config
import com.typesafe.scalalogging.LazyLogging
import org.apache.kafka.clients.producer.ProducerRecord
import utils.ApplicationConstants.{EMPTY_STRING, LATEST, SSL}
import zio.ZLayer
import zio.kafka.producer.{Producer, ProducerSettings}

import scala.collection.convert.ImplicitConversions.`collection AsScalaIterable`
import scala.util.{Failure, Success, Try}

object ProducerUtils extends LazyLogging {
  def getProducerSettings(config: Config): ProducerSettings = {
    val brokerList: List[String] = config.getString("brokerList").split(",").toList

    val autoOffsetReset: String = Try(config.getString("truststoreLocation")) match {
      case Success(value: String) => value
      case Failure(exception) => {
        logger.warn("Didnt' Find truststoreLocation value. Setting it as Empty String")
        LATEST
      }
    }

    val securityProtocol: String = Try(config.getString("truststoreLocation")) match {
      case Success(value: String) => value
      case Failure(exception) => {
        logger.warn("Didnt' Find truststoreLocation value. Setting it as Empty String")
        SSL
      }
    }

    val trustStoreLocation: String = Try(config.getString("truststoreLocation")) match {
      case Success(value: String) => value
      case Failure(exception) => {
        logger.warn("Didnt' Find truststoreLocation value. Setting it as Empty String")
        EMPTY_STRING
      }
    }

    val trustStorePassword: String = Try(config.getString("truststorePassword")) match {
      case Success(value: String) => value
      case Failure(exception) => {
        logger.warn("Didnt' Find truststorePassword value. Setting it as Empty String")
        EMPTY_STRING
      }
    }

    val keyStoreLocation: String = Try(config.getString("keystoreLocation")) match {
      case Success(value: String) => value
      case Failure(exception) => {
        logger.warn("Didnt' Find keystoreLocation value. Setting it as Empty String")
        EMPTY_STRING
      }
    }

    val keyStorePassword: String = Try(config.getString("keystorePassword")) match {
      case Success(value: String) => value
      case Failure(exception) => {
        logger.warn("Didnt' Find keystorePassword value. Setting it as Empty String")
        EMPTY_STRING
      }
    }

    val keyPassword: String = Try(config.getString("keyPassword")) match {
      case Success(value: String) => value
      case Failure(exception) => {
        logger.warn("Didnt' Find keyPassword value. Setting it as Empty String")
        EMPTY_STRING
      }
    }

    val endPointIdentificationAlgo: String = Try(config.getString("endpointIdentificationAlgorithm")) match {
      case Success(value: String) => value
      case Failure(exception) => {
        logger.warn("Didnt' Find endpointIdentificationAlgorithm value. Setting it as Empty String")
        EMPTY_STRING
      }
    }

    ProducerSettings(brokerList)
      .withProperty("auto.offset.reset", autoOffsetReset)
//      .withProperty("security.protocol", securityProtocol)
//      .withProperty("ssl.truststore.location", trustStoreLocation)
//      .withProperty("ssl.truststore.password", trustStorePassword)
//      .withProperty("ssl.keystore.location", keyStoreLocation)
//      .withProperty("ssl.keystore.password", keyStorePassword)
//      .withProperty("ssl.key.password", keyPassword)
      .withProperty("ssl.endpoint.identification.algorithm", endPointIdentificationAlgo)
  }

  def getProducerLayer(producerSettings: ProducerSettings): ZLayer[Any, Throwable, Producer] = {
      ZLayer.scoped(
        Producer.make(
          producerSettings
        )
      )
  }

  def getProducerRecord(config: Config): ProducerRecord[Int, String] = {
    val topicName: String = config.getString("topic")
    val key: Int = config.getInt("key")
    val message: String = config.getString("message")
    new ProducerRecord(topicName, key, message)
  }
}
