package com.cory.web

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import com.cory.core.Greeting

trait JsonSupport extends SprayJsonSupport {
  import spray.json.DefaultJsonProtocol._
  implicit val greetingFormat = jsonFormat1(Greeting)
}