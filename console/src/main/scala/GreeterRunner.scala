package com.cory.console

import akka.actor.{ActorRef, ActorSystem}
import com.cory.core.GreetingTranslator.GreetingRequest
import com.cory.core.{Greeter, GreetingTranslator}

import scala.concurrent.duration._

object GreeterRunner extends App {
  val system: ActorSystem = ActorSystem("GreeterSystem")

  def createGreeter(message: String): ActorRef = {
    system.actorOf(Greeter.props(message), s"${message.replaceAll("\\s+", "")}Greeter")
  }

  val dialectMap = Map(
    "cowboy" -> createGreeter("Howdy"),
    "basic" -> createGreeter("Hello"),
    "butler" -> createGreeter("Good day")
  )
  val greetingTranslator = system.actorOf(GreetingTranslator.props(dialectMap))

  val greetingLogger = system.actorOf(GreetingLogger.props(greetingTranslator))
  val greetings = List(
    ("Partner", "cowboy"),
    ("Buckaroo", "cowboy"),
    ("Mr. Pickles", "basic"),
    ("Dr. Eugene Mustardpants II", "butler"),
  )

  for ((name, dialect) <- greetings) { greetingLogger ! GreetingRequest(dialect, name) }

  system.scheduler.scheduleOnce(5 seconds) {
    println("Terminating app")
    system.terminate()
  }(system.dispatcher)
}
