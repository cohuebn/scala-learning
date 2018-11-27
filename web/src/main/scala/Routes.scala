package com.cory.web

import akka.actor.ActorRef
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.pattern.AskSupport
import akka.util.Timeout
import com.cory.core.Greeting

import scala.concurrent.duration._

trait JsonSupport extends SprayJsonSupport {
  import spray.json.DefaultJsonProtocol._
  implicit val greetingFormat = jsonFormat1(Greeting)
}

trait Routes extends JsonSupport with AskSupport {
  implicit val timeout: Timeout = 3.seconds

  def routes(greetingTranslator: ActorRef): Route = pathPrefix("greetings") {
    def toGreetingResponse(greetingMagnet: GreetingRequestMagnet) = {
      complete { (greetingTranslator ? greetingMagnet.apply).mapTo[Greeting] }
    }

    pathPrefix("dialects") {
      path("random" / Segment) {
        name => get(toGreetingResponse(name))
      } ~
      path(Segment / Segment).as(ValidatedGreetingRequest) {
        validatedRequest => get(toGreetingResponse(validatedRequest))
      }
    }
  }
}