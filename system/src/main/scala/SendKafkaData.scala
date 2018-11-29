package com.cory.system

import java.text.SimpleDateFormat
import java.util.Date

import akka.Done
import akka.actor.ActorSystem
import akka.kafka.ProducerSettings
import akka.kafka.scaladsl.Producer
import akka.stream.scaladsl.Source
import akka.stream.{ActorMaterializer, Materializer}
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.StringSerializer

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

import fabricator.Words

object SendKafkaData extends App {
  implicit val system: ActorSystem = ActorSystem("KafkaProducer")
  implicit val materializer: Materializer = ActorMaterializer()

  val producerSettings = ProducerSettings(system, new StringSerializer, new StringSerializer)
    .withBootstrapServers("localhost:9092")

  val done: Future[Done] = Source(1 to 10)
    .throttle(1, 2 seconds)
    .map(x => new ProducerRecord[String, String]("test", s"${Words().sentence(3)}Time is ${now}"))
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

  def now = {
    val formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
    formatter.format(new Date)
  }
}
