package com.cory.web

import com.typesafe.config.ConfigFactory

object Config {
  lazy val config = ConfigFactory.parseResources("defaults.conf").resolve()
  lazy val apiName = config.getString("conf.apiName")
  lazy val apiPort = config.getInt("conf.apiPort")
}
