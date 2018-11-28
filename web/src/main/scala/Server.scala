package com.cory.web

import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import com.cory.core.{Greeter, GreetingTranslator}
import com.cory.web.Config.{apiPort, apiName}
import com.cory.core.Dialects.dialectMap

import scala.io.StdIn

object Server extends App {
  implicit val system = ActorSystem("GreeterWebApp")
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher

  def createGreeter(message: String): ActorRef = {
    system.actorOf(Greeter.props(message), s"${message.replaceAll("\\s+", "")}Greeter")
  }

  val greeterMap = dialectMap map { case (dialect, greeting) => dialect -> createGreeter(greeting) }
  val greetingTranslator = system.actorOf(GreetingTranslator.props(greeterMap))

  val (url, port) = ("localhost", apiPort)
  val greetingController = new GreetingController(greetingTranslator)
  val bindingFuture = Http().bindAndHandle(greetingController.routes, url, port)

  println(s"Running the $apiName server @ http://$url:$port/\nPress RETURN to stop...")
  StdIn.readLine()
  bindingFuture
    .flatMap(_.unbind())
    .onComplete(_ => system.terminate())
}
