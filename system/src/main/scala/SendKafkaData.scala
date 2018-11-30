package com.cory.system

import akka.Done
import akka.actor.ActorSystem
import akka.kafka.ProducerSettings
import akka.kafka.scaladsl.Producer
import akka.stream.scaladsl.Source
import akka.stream.{ActorMaterializer, Materializer}
import fabricator.Contact
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.StringSerializer

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

object SendKafkaData extends App {
  implicit val system: ActorSystem = ActorSystem("KafkaProducer")
  implicit val materializer: Materializer = ActorMaterializer()
  lazy val contactGenerator = Contact()

  val producerSettings = ProducerSettings(system, new StringSerializer, new StringSerializer)
    .withBootstrapServers(Config.kafkaBootstrapServer)

  val done: Future[Done] = Source(1 to 10)
    .throttle(1, 300 milliseconds)
    .map(_ => new ProducerRecord[String, String](Config.greetingTopic, s"${contactGenerator.fullName(true, false)}"))
    .log("Random greeting", x => x.value)
    .runWith(Producer.plainSink(producerSettings))

  implicit val ec: ExecutionContext = system.dispatcher
  done.onComplete {
    case Success(_) =>
      println("Successfully talked to Kafka")
      system.terminate()
    case Failure(exception) =>
      println("Bad stuff happened\n" + exception.toString)
      system.terminate()
  }
}
