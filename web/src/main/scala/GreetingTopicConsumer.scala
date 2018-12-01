package com.cory.web

import akka.actor.{Actor, ActorLogging, Props}
import akka.kafka.scaladsl.Consumer
import akka.kafka.{ConsumerSettings, Subscriptions}
import akka.stream.Materializer
import akka.stream.scaladsl.Sink
import com.cory.core.Greeting
import com.cory.web.GreetingTopicConsumer.GreetingTopicConsumerNewRequest

import scala.concurrent.ExecutionContextExecutor
import scala.concurrent.duration._
import scala.util.{Failure, Success}

object GreetingTopicConsumer {
  def props(consumerSettings: ConsumerSettings[String, String])(implicit materializer: Materializer): Props =
    Props(new GreetingTopicConsumer(consumerSettings)(materializer))
  object GreetingTopicConsumerNewRequest

}

class GreetingTopicConsumer(consumerSettings: ConsumerSettings[String, String])(implicit materializer: Materializer) extends Actor with ActorLogging {
  implicit val ec: ExecutionContextExecutor = context.dispatcher
  override def receive: Receive = {
    case GreetingTopicConsumerNewRequest => {
      val done = Consumer.plainSource(consumerSettings, Subscriptions.topics(Config.greetingTopic))
        .takeWithin(5 seconds)
        .runWith(Sink.foreach(x => log.warning(x.value())))

      val senderRef = sender()
      done.onComplete {
        case Success(_) => {
          log.warning("I'm successfully done")
          senderRef ! Greeting("Successfully done")
        }
        case Failure(_) => {
          log.warning("I'm failfully done")
          senderRef ! Greeting("Failed")
        }
      }
    }
  }
}