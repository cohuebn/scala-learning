package com.cory.web

import com.cory.core.{Config => CoreConfig}

object Config extends CoreConfig {
  lazy val apiName = config.getString("conf.apiName")
  lazy val apiPort = config.getInt("conf.apiPort")
}
