package com.cory.core

import com.typesafe.config.ConfigFactory

abstract class Config() {
  def getConfig = ConfigFactory.parseResources("application.conf").resolve()
  lazy val config = getConfig
}
