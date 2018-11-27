package com.cory.web

import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import com.cory.core.{Greeter, GreetingTranslator}
import com.cory.web.Config.{apiPort, apiName}

import scala.io.StdIn

object Server extends App with Routes {
  implicit val system = ActorSystem("GreeterWebApp")
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher

  def createGreeter(message: String): ActorRef = {
    system.actorOf(Greeter.props(message), s"${message.replaceAll("\\s+", "")}Greeter")
  }

  val dialectMap = Map(
    "cowboy" -> createGreeter("Howdy"),
    "basic" -> createGreeter("Hello"),
    "butler" -> createGreeter("Good day")
  )
  val greetingTranslator = system.actorOf(GreetingTranslator.props(dialectMap))

  val (url, port) = ("localhost", apiPort)
  val bindingFuture = Http().bindAndHandle(routes(greetingTranslator), url, port)

  println(s"Running the $apiName server @ http://$url:$port/\nPress RETURN to stop...")
  StdIn.readLine()
  bindingFuture
    .flatMap(_.unbind())
    .onComplete(_ => system.terminate())
}
