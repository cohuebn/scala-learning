package com.cory.core.test

import akka.testkit.{ImplicitSender, TestProbe}
import com.cory.core.Greeter.Greet
import com.cory.core.GreetingTranslator
import com.cory.core.GreetingTranslator.GreetingRequest
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

class GreetingTranslatorSpec() extends BaseSpec[GreeterSpec] {
  "A GreeterTranslator actor" should {
    val (greeter1, greeter2) = (TestProbe(), TestProbe())
    val testCases = Map(
      "greeting1" -> greeter1,
      "greeting2" -> greeter2
    )
    val dialectMap = testCases.map({ case (dialect: String, probe: TestProbe) => dialect -> probe.testActor })

    testCases.foreach {
      case (dialect, greeter) => {
        s"send the correct message to the sender when dialect $dialect" in {
          val name = "Marge"
          val request = GreetingRequest(dialect, name)

          val greetingTranslator = system.actorOf(GreetingTranslator.props(dialectMap))
          greetingTranslator ! request

          greeter.expectMsg(Greet(name, testActor))
        }
      }
    }
  }
}