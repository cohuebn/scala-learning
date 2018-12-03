package com.cory.core.test

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit}
import com.typesafe.config.ConfigFactory
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

import scala.reflect.ClassTag
import scala.reflect._

class BaseSpec[T: ClassTag]()
  extends TestKit(ActorSystem(classTag[T].runtimeClass.getSimpleName(), ConfigFactory.parseString("""akka.loggers = ["akka.testkit.TestEventListener"]""")))
    with ImplicitSender
    with Matchers
    with WordSpecLike
    with BeforeAndAfterAll {

  override def afterAll: Unit = {
    shutdown(system)
  }
}