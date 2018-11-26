package com.cory.core.tests

import akka.testkit.{ImplicitSender, TestProbe}
import com.cory.core.{Greeter, Greeting}
import com.cory.core.Greeter._
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

import scala.concurrent.duration._
import scala.language.postfixOps

class GreeterSpec() extends BaseSpec("GreeterSpec") with ImplicitSender
  with Matchers
  with WordSpecLike
  with BeforeAndAfterAll {

  override def afterAll: Unit = {
    shutdown(system)
  }

  "A Greeter Actor" should {
    "send a greeting message to the respondTo actor when given" in {
      val message = "yo"
      val respondTo = TestProbe()
      val helloGreeter = system.actorOf(Greeter.props(message))

      val person = "Marge"
      helloGreeter ! Greet(person, respondTo.testActor)

      respondTo.expectMsg(500 millis, Greeting("yo, Marge"))
    }
  }
}
