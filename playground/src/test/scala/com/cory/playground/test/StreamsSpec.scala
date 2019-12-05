package com.cory.playground.test

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Flow, Sink, Source}
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{Matchers, WordSpec}

class StreamsSpec extends WordSpec with Matchers with ScalaFutures {

  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()

  "streams" should {
    "allow simple sequence transformations" in {
      val source = Source(1 to 3)

      val intToStringFlow = Flow[Int].map(x => s"processed $x")

      val result = source.via(intToStringFlow).runWith(Sink.seq)

      result.futureValue should contain theSameElementsInOrderAs(Seq("processed 1", "processed 2", "processed 3"))
    }

    "allow aggregating stream results" in {
      val source = Source(1 to 3)

      val summingSink = Sink.fold[Int, Int](0) { (sum, current) => sum + current }

      val result = source.runWith(summingSink)

      result.futureValue shouldBe 6
    }
  }
}
