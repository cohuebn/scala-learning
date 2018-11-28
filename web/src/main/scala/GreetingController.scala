package com.cory.web

import akka.actor.ActorRef
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.cory.core.Greeting

class GreetingController(greetingTranslator: ActorRef) extends Controller {
  private def toGreetingResponse(greetingMagnet: GreetingRequestMagnet): Route = {
    complete { (greetingTranslator ? greetingMagnet.apply).mapTo[Greeting] }
  }

  lazy val routes: Route = pathPrefix("greetings") {
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
