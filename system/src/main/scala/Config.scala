package com.cory.system

import com.cory.core.{Config => CoreConfig}

object Config extends CoreConfig {
  lazy val kafkaBootstrapServer = config.getString("conf.kafka.bootstrapServer")
  lazy val greetingTopic = config.getString("conf.kafka.greetingTopic")
}
