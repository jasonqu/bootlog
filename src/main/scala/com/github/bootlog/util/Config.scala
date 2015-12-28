package com.github.bootlog.util

import com.typesafe.config.{Config, ConfigFactory}

object Config {

  var conf : Config = ConfigFactory.load()

  def message(key: String): String = {
    conf.getString(key)
  }
  
  lazy val rootPath = if(conf.getString("rootPath").isEmpty()) {
    "/"
  } else {
    "/" + conf.getString("rootPath") + "/"
  }
  
  def path(relative : String) : String = {
    rootPath + relative
  }
}