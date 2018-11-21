package com.cory.console

import akka.actor.{ActorRef, ActorSystem}
import com.cory.core.Greeter.Greet
import com.cory.core.{Greeter, Printer}

import scala.concurrent.duration._

object GreeterRunner extends App {
  val system: ActorSystem = ActorSystem("GreeterSystem")

  val printer: ActorRef = system.actorOf(Printer.props, "printerActor")

  def createGreeter(message: String): ActorRef = {
    system.actorOf(Greeter.props(message, printer), s"${message.replaceAll("\\s+", "")}Greeter")
  }

  val (howdyGreeter, helloGreeter, goodDayGreeter) = (
    createGreeter("Howdy"),
    createGreeter("Hello"),
    createGreeter("Good day")
  )

  val greetings = Map(
    "Partner" -> howdyGreeter,
    "Buckaroo" -> howdyGreeter,
    "Mr. Pickles" -> helloGreeter,
    "Dr. Eugene Mustardpants II" -> goodDayGreeter,
  )

  for ((name, greeter) <- greetings) { greeter ! Greet(name) }

  system.scheduler.scheduleOnce(5 seconds) {
    println("Terminating app")
    system.terminate()
  }(system.dispatcher)
}
