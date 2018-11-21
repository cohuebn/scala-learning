package com.cory.web

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer

import scala.io.StdIn

object Server extends App with Routes {
  implicit val system = ActorSystem("GreeterWebApp")
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher

  val (url, port) = ("localhost", 1307)
  val bindingFuture = Http().bindAndHandle(routes, url, port)

  println(s"Running the server @ http://$url:$port/\nPress RETURN to stop...")
  StdIn.readLine()
  bindingFuture
    .flatMap(_.unbind())
    .onComplete(_ => system.terminate())
}
