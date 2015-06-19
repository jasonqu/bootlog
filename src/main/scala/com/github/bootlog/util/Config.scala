package com.github.bootlog.util

import com.typesafe.config.ConfigFactory
object Config {

  val conf = ConfigFactory.load();

  def message(key: String): String = {
    conf.getString(key)
  }
  
  val rootPath = if(conf.getString("rootPath").isEmpty()) {
    ""
  } else {
    "/" + conf.getString("rootPath") + "/"
  }
  
  def path(relative : String) : String = {
    rootPath + relative
  }
}