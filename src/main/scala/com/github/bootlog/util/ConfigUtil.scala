package com.github.bootlog.util

import com.typesafe.config.{Config, ConfigFactory}
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat

object ConfigUtil {

  var conf: Config = ConfigFactory.load()

  def message(key: String): String = {
    conf.getString(key)
  }

  lazy val rootPath =
    if (conf.getString("rootPath").isEmpty) {
      "/"
    } else {
      "/" + conf.getString("rootPath") + "/"
    }

  def path(relative: String): String = {
    rootPath + relative
  }

  lazy val extraNavBar: List[(String, String)] = {
    import collection.JavaConversions._
    conf.getConfigList("extra_nav_list").toList.map {entry =>
      (entry.getString("url"), entry.getString("name"))
    }
  }

  var assets : Seq[String] = Seq[String]()
  lazy val CssList = assets.filter(_.endsWith(".css"))
  lazy val JsList = assets.filter(_.endsWith(".js"))

  def displayDate(format: String, date: DateTime): String = {
    DateTimeFormat.forPattern(format).print(date)
  }
}