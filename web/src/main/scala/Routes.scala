package com.cory.web

import akka.actor.ActorRef
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.pattern.AskSupport
import akka.util.Timeout
import com.cory.core.Greeting
import com.cory.core.GreetingTranslator.GreetingRequest

import scala.concurrent.duration._
import scala.util.Random

trait JsonSupport extends SprayJsonSupport {
  import spray.json.DefaultJsonProtocol._
  implicit val greetingFormat = jsonFormat1(Greeting)
}

trait Routes extends JsonSupport with AskSupport {
  implicit val timeout: Timeout = 3.seconds
  val dialects = List("basic", "cowboy", "butler")
  def routes(greetingTranslator: ActorRef): Route = pathPrefix("greetings") {
    path("random" / Remaining) {
      name => get {
        val dialect = Random.shuffle(dialects).head
        onSuccess(greetingTranslator ? GreetingRequest(dialect, name)) {
          case greeting: Greeting => complete(greeting)
        }
      }
    } ~
    path(s"${dialects.mkString("|")}".r / Remaining) {
      (dialect: String, name: String) => get {
        onSuccess(greetingTranslator ? GreetingRequest(dialect, name)) {
          case greeting: Greeting => complete(greeting)
        }
      }
    }
  }
}