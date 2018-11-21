package com.cory.web

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._
import com.cory.core.Printer.Greeting

trait JsonSupport extends SprayJsonSupport {
  import spray.json.DefaultJsonProtocol._
  implicit val greetingFormat = jsonFormat1(Greeting)
}

trait Routes extends JsonSupport {
  lazy val routes: Route = path("greetings" / Remaining) {
    name => get { complete { Greeting(name) } }
  }
}