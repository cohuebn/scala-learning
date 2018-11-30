package com.cory.web

import akka.actor.{Actor, ActorLogging, Props}
import akka.kafka.{ConsumerSettings, Subscriptions}
import akka.kafka.scaladsl.Consumer
import akka.stream.{ActorMaterializer, Materializer}
import akka.stream.scaladsl.Sink
import com.cory.web.GreetingTopicConsumer.GreetingTopicConsumerNewRequest

import scala.concurrent.ExecutionContextExecutor
import scala.util.{Failure, Success}
import scala.concurrent.duration._

object GreetingTopicConsumer {
  def props(consumerSettings: ConsumerSettings[String, String]): Props = Props(new GreetingTopicConsumer(consumerSettings))
  object GreetingTopicConsumerNewRequest

}

class GreetingTopicConsumer(consumerSettings: ConsumerSettings[String, String]) extends Actor with ActorLogging {
  implicit val materializer: Materializer = ActorMaterializer()
  implicit val ec: ExecutionContextExecutor = context.dispatcher
  override def receive: Receive = {
    case GreetingTopicConsumerNewRequest => {
      val done = Consumer.plainSource(consumerSettings, Subscriptions.topics(Config.greetingTopic))
        .takeWithin(5 seconds)
        .runWith(Sink.foreach(x => log.warning(x.value())))

      done onComplete {
        case Success(_) => {
          sender() ! "I'm done"
        }
        case Failure(_) => sender() ! "I'm done"
      }
    }
  }
}