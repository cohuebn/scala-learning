package com.cory.console

import akka.actor.{ActorRef, ActorSystem}
import com.cory.core.Greeter.{Greet, WhoToGreet}
import com.cory.core.{Greeter, Printer}
import scala.concurrent.duration._

object AkkaRunner extends App {
  val system: ActorSystem = ActorSystem("helloAkka")

  val printer: ActorRef = system.actorOf(Printer.props, "printerActor")

  val howdyGreeter: ActorRef =
    system.actorOf(Greeter.props("Howdy", printer), "howdyGreeter")
  val helloGreeter: ActorRef =
    system.actorOf(Greeter.props("Hello", printer), "helloGreeter")
  val goodDayGreeter: ActorRef =
    system.actorOf(Greeter.props("Good day", printer), "goodDayGreeter")

  val greetings = Map(
    "Partner" -> howdyGreeter,
    "Buckaroo" -> howdyGreeter,
    "Mr. Pickles" -> helloGreeter,
    "Dr. Eugene Mustardpants II" -> goodDayGreeter,
  )

  greetings.foreach {
    case (name, greeter) =>
      greeter ! WhoToGreet(name)
      greeter ! Greet
  }

  system.scheduler.scheduleOnce(10 seconds) {
    println("Terminating app")
    system.terminate()
  }(system.dispatcher)
}
