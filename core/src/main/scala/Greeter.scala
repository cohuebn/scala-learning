package com.cory.core

import akka.actor.{ Actor, ActorRef, Props }

object Greeter {
  def props(message: String, printer: ActorRef): Props = Props(new Greeter(message, printer))

  final case class Greet(who: String)
}

class Greeter(message: String, greetingProcessor: ActorRef) extends Actor {
  import Greeter._
  import Printer.Greeting

  def receive = {
    case Greet(who) =>
      greetingProcessor ! Greeting(s"$message, $who")
  }
}