package com.cory.console

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import com.cory.core.GreetingTranslator.GreetingRequest

object GreetingLogger {
  def props(greetingTranslator: ActorRef): Props = Props(new GreetingLogger(greetingTranslator))
}

class GreetingLogger(greetingTranslator: ActorRef) extends Actor with ActorLogging {
  import com.cory.core.Greeting

  override def receive: Receive = {
    case Greeting(greeting) => log.info(greeting)
    case greetingRequest: GreetingRequest => greetingTranslator ! greetingRequest
  }
}
