package com.cory.web

import akka.actor.{Actor, ActorRef, Props}
import akka.kafka.scaladsl.Consumer
import akka.stream.Materializer
import akka.stream.scaladsl.{Sink, Source}
import akka.util.Timeout
import com.cory.core.Greeting
import com.cory.core.GreetingTranslator.GreetingRequest
import com.cory.web.GreetingTopicConsumer.GreetingTopicConsumerNewRequest
import org.apache.kafka.clients.consumer.ConsumerRecord

import scala.concurrent.duration._

object GreetingTopicConsumer {
  def props(consumer: Source[ConsumerRecord[String, String], Consumer.Control],
            greetingTranslator: ActorRef)
           (implicit materializer: Materializer): Props =
    Props(new GreetingTopicConsumer(consumer, greetingTranslator)(materializer))
  object GreetingTopicConsumerNewRequest
}

class GreetingTopicConsumer(consumer: Source[ConsumerRecord[String, String], Consumer.Control],
                            greetingTranslator: ActorRef)
                           (implicit materializer: Materializer) extends Actor {
  implicit val timeout: Timeout = 1 second
  private val batchSize = 10

  private var greetings = List[Greeting]()
  private var originalSender: Option[ActorRef] = None

  override def receive: Receive = {
    case GreetingTopicConsumerNewRequest => {
      originalSender = Option(sender())
      consumer
        .take(batchSize)
        .takeWithin(5 seconds)
        .map(consumerRecord => GreetingRequest("basic", consumerRecord.value(), Option(context.self)))
        .runWith(Sink.foreach(greetingRequest => greetingTranslator ! greetingRequest))
    }
    case greeting: Greeting =>
      greetings = greetings :+ greeting
      if (greetings.length == batchSize)
        originalSender.foreach(x => x ! greetings)
  }
}