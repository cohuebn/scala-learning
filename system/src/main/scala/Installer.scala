package com.cory.system

import java.io.File
import java.net.URL
import sys.process._

object Installer extends App {
  def installKafka(): Unit = {
    new URL("http://mirror.reverse.net/pub/apache/kafka/2.1.0/kafka_2.11-2.1.0.tgz") #>
      new File("kafka.tgz") !!
  }

  installKafka
}