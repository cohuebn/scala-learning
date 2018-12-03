package com.cory.web

import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.Http
import akka.kafka.scaladsl.Consumer
import akka.kafka.{ConsumerSettings, Subscriptions}
import akka.stream.ActorMaterializer
import com.cory.core.Greeter
import com.cory.web.Config.{apiName, apiPort}
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.StringDeserializer

import scala.concurrent.Await
import scala.concurrent.duration.Duration

object Server extends App {
  implicit val system = ActorSystem("GreeterWebApp")
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher

  def createGreeter(message: String): ActorRef = {
    system.actorOf(Greeter.props(message), s"${message.replaceAll("\\s+", "")}Greeter")
  }
  val consumerSettings = ConsumerSettings(system, new StringDeserializer, new StringDeserializer)
    .withBootstrapServers(Config.kafkaBootstrapServer)
    .withGroupId("greeting-group")
    .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")
  val consumer = Consumer.plainSource(consumerSettings, Subscriptions.topics(Config.greetingTopic))

  val (url, port) = ("localhost", apiPort)
  val greetingController = new GreetingController(consumer)
  val bindingFuture = Http().bindAndHandle(greetingController.routes, url, port)

  println(s"Running the $apiName server @ http://$url:$port/\nPress ctrl+c to stop...")

  Await.result(system.whenTerminated, Duration.Inf)
  sys.addShutdownHook {
    println("Shutting down system")
    system.terminate()
  }
}
