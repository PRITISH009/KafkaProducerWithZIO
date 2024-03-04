package utils

import com.typesafe.scalalogging.LazyLogging

object ConfigUtils extends LazyLogging {
  case class Arg(key: String, value: String)

  def extractArg(arg: String): Arg = {
    val pattern = "-D(.*)=(.*)".r
    arg match {
      case pattern(key, value) => Arg(key, value)
    }
  }

}
