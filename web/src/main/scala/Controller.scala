package com.cory.web

import akka.http.scaladsl.server.Route
import akka.pattern.AskSupport
import akka.util.Timeout

import scala.concurrent.duration._

abstract class Controller extends JsonSupport with AskSupport {
  implicit val timeout: Timeout = 3.seconds

  val routes: Route
}
