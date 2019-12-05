package com.cory.playground

import akka.actor.ActorSystem
import akka.stream.{ActorMaterializer, IOResult}
import akka.stream.scaladsl.{Flow, Sink, Source, StreamConverters}
import akka.util.ByteString

import scala.concurrent.Future
import scala.util.{Failure, Success}

import scala.concurrent.duration._

object Streams {
  implicit val system = ActorSystem()
  implicit val ec = system.dispatcher
  implicit val materializer = ActorMaterializer()

  def runStdInStream = {
    println("Start entering text followed by a newline. Type 'quit' to quit")
    val stdinSource: Source[ByteString, Future[IOResult]] = StreamConverters.fromInputStream(() => System.in)

    val uppercaseUntilQuitFlow = Flow[ByteString].map(_.utf8String.toUpperCase())
      .takeWhile(_.trim != "QUIT")

    val printSink = Sink.foreach[String](x => println(s"processed: $x"))

    val fullStream = stdinSource.via(uppercaseUntilQuitFlow).runWith(printSink)
    fullStream.onComplete {
      case Success(_) => println("stream finished successfully")
      case Failure(err) => println("stream failed miserably", err.toString)
    }
  }

  def runBackpressuredStream = {
    val lotsOfNumbersSource = Source(1 to 150)

    val bottleneck = Flow[Int].throttle(5, 3 seconds)

    val printSink = Sink.foreach[Int](x => println(s"processed: $x"))

//    val fullStream = lotsOfNumbersSource.runWith(printSink)
    val fullStream = lotsOfNumbersSource.via(bottleneck).runWith(printSink)
    fullStream.onComplete {
      case Success(_) => println("stream finished successfully")
      case Failure(err) => println("stream failed miserably", err.toString)
    }
  }
}
