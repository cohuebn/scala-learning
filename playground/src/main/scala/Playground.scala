package com.cory.playground

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Sink

object Playground extends App {
  Tests.runStreamScenario
}

object Tests {
  def runPolymorphismScenario: Traversable[Unit] = {
    val baseClassInstances = List(new Subclass1, new Subclass2)

    BaseClassProcessor.mapAll(baseClassInstances) {
      baseClassInstance => baseClassInstance.printName()
    }

    val barkerInstances = List(new Subclass1, new Subclass1)
    BaseClassProcessor.mapAll(barkerInstances) {
      barkerInstance => barkerInstance.iBark()
    }
  }

  def runTypeClassScenario: Unit = {
    def add[T](vals: Traversable[T])(implicit ev: Addable[T]): T = {
      vals.reduce(ev.add)
    }

    val ints = List(1, 2, 3)
    println(s"Added ints: ${add(ints)}")

    import com.cory.playground.forImports.ForImports.ImprovedIntAdder
    println(s"Added ints with improved adder: ${add(ints)}")

    val strings = List("this", "is", "a", "sentence")
    println(s"Added strings: ${add(strings)}")

    implicit object improvedStringAdder extends Addable[String] {
      override def add(val1: String, val2: String): String = {
        val1.concat("! ").concat(val2)
      }
    }

    println(s"Added strings with improved adder: ${add(strings)}")
  }

  def runStreamScenario: Unit = {
    import scala.concurrent.ExecutionContext.Implicits.global
    implicit val system = ActorSystem("LearningToStream")
    implicit val materializer: ActorMaterializer = ActorMaterializer()

    val seqSink = Sink.seq[Int]
    val result = Streams.addFive(Streams.range(10)).runWith(seqSink)
    result.foreach(println(_))
    system.terminate()
  }
}