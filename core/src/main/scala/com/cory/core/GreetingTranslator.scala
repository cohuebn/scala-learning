package com.cory.core

import akka.actor.{Actor, ActorRef, Props}

object GreetingTranslator {
  def props(dialectMap: Map[String, ActorRef]): Props = Props(new GreetingTranslator(dialectMap))
  final case class GreetingRequest(dialect: String, name: String, replyTo: Option[ActorRef] = None)
}

class GreetingTranslator(dialectMap: Map[String, ActorRef]) extends Actor {
  import GreetingTranslator.GreetingRequest
  import com.cory.core.Greeter.Greet

  override def receive: Receive = {
    case GreetingRequest(dialect, name, actorRef) if dialectMap.contains(dialect) =>
      dialectMap(dialect) ! Greet(name, actorRef.getOrElse(sender()))
  }
}