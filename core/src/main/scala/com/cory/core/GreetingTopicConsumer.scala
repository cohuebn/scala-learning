package com.cory.core

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.kafka.scaladsl.Consumer
import akka.stream.Materializer
import akka.stream.scaladsl.{Sink, Source}
import akka.util.Timeout
import com.cory.core.GreetingTopicConsumer.LatestGreetingsRequest
import com.cory.core.GreetingTranslator.GreetingRequest
import org.apache.kafka.clients.consumer.ConsumerRecord

import scala.concurrent.duration._

object GreetingTopicConsumer {
  def props(consumer: Source[ConsumerRecord[String, String], Consumer.Control],
            greetingTranslator: ActorRef)
           (implicit materializer: Materializer): Props =
    Props(new GreetingTopicConsumer(consumer, greetingTranslator)(materializer))

  final case class LatestGreetingsRequest(count: Int)
}

class GreetingTopicConsumer(consumer: Source[ConsumerRecord[String, String], Consumer.Control],
                            greetingTranslator: ActorRef)
                           (implicit materializer: Materializer) extends Actor
  with ActorLogging {
  implicit val timeout: Timeout = 1 second

  private var greetings = List[Greeting]()
  private var takeSize: Option[Int] = None
  private var replyTo: Option[ActorRef] = None

  override def receive: Receive = {
    case request: LatestGreetingsRequest => {
      replyTo = Option(sender())
      val latestEmissions = consumer
        .takeWithin(15 seconds)
        .log("received", _.value())
        .runWith(Sink.takeLast(request.count))

      Source.fromFuture(latestEmissions).runWith(Sink.foreach(consumerRecords => {
        log.info(s"consumerRecords: $consumerRecords")
        takeSize = Option(consumerRecords.length)
        consumerRecords.map(consumerRecord => GreetingRequest("basic", consumerRecord.value(), Option(context.self)))
          .foreach(greetingRequest => {
            log.info(s"Sending: $greetingRequest")
            greetingTranslator ! greetingRequest
          })
      }))
    }
    case greeting: Greeting =>
      greetings = greetings :+ greeting
      takeSize.filter(x => x == greetings.length).foreach(_ => replyTo.get ! greetings)
  }
}