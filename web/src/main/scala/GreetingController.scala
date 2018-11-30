package com.cory.web

import akka.actor.ActorRef
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.cory.core.Greeting
import com.cory.web.GreetingTopicConsumer.GreetingTopicConsumerNewRequest
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
import io.circe.generic.auto._

class GreetingController(greetingTranslator: ActorRef, greetingTopicConsumer: ActorRef) extends Controller {
  private def toGreetingResponse(greetingMagnet: GreetingRequestMagnet): Route = {
    complete {
      (greetingTranslator ? greetingMagnet.apply).mapTo[Greeting]
    }
  }

  lazy val routes: Route = pathPrefix("greetings") {
    pathPrefix("dialects") {
      path("random" / Segment) {
        name => get(toGreetingResponse(name))
      } ~
      path(Segment / Segment).as(ValidatedGreetingRequest) {
        validatedRequest => get(toGreetingResponse(validatedRequest))
      }
    } ~
    path("new") {
      post {
        complete {
          (greetingTopicConsumer ? GreetingTopicConsumerNewRequest).mapTo[String]
        }
      }
    }
  }
}
