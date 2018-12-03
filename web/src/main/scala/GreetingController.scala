package com.cory.web

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.kafka.scaladsl.Consumer
import akka.stream.scaladsl.Source
import com.cory.core.Dialects.dialectMap
import com.cory.core.{Greeting, GreetingTranslator}
import com.cory.web.GreetingTopicConsumer.LatestGreetingsRequest
import com.cory.web.Server.createGreeter
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
import io.circe.generic.auto._
import org.apache.kafka.clients.consumer.ConsumerRecord

class GreetingController(consumer: Source[ConsumerRecord[String,String], Consumer.Control]) extends Controller {
  import com.cory.web.Server.{system, materializer}

  val greeterMap = dialectMap map { case (dialect, greeting) => dialect -> createGreeter(greeting) }
  val greetingTranslator = system.actorOf(GreetingTranslator.props(greeterMap))

  private def toGreetingResponse(greetingMagnet: GreetingRequestMagnet): Route = {
    complete {
      (greetingTranslator ? greetingMagnet.apply).mapTo[Greeting]
    }
  }

  private def toGreetingsResponse(latestGreetingsRequest: LatestGreetingsRequest) = {
    complete {
      val greetingTopicConsumer = system.actorOf(GreetingTopicConsumer.props(consumer, greetingTranslator))
      (greetingTopicConsumer ? latestGreetingsRequest).mapTo[List[Greeting]]
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
    pathPrefix("latest") {
      path(IntNumber) {
        count => post(toGreetingsResponse(LatestGreetingsRequest(count)))
      } ~
      post(toGreetingsResponse(LatestGreetingsRequest(10)))
    }
  }
}
