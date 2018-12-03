package com.cory.core

import akka.actor.{Actor, ActorRef, Props}

object Greeter {
  def props(message: String): Props = Props(new Greeter(message))

  final case class Greet(who: String, respondTo: ActorRef)
}

class Greeter(message: String) extends Actor {
  import Greeter._

  def receive = {
    case Greet(who, respondTo) => respondTo ! Greeting(s"$message, $who")
  }
}