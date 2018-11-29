package com.cory.playground

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Sink

object Playground extends App {
  Tests.runPolymorphismScenario
}

object Tests {
  def runPolymorphismScenario: Unit = {
    val baseClassInstances = List(new Subclass1("Woof"), new Subclass2)

    BaseClassProcessor.printAndMap(baseClassInstances) {
      baseClassInstance => baseClassInstance.printName()
    }

    val barkerInstances = List(new Subclass1("Yip"), new Subclass1("Weoowww!"))
    val barkSounds = BaseClassProcessor.printAndMap(barkerInstances) {
      _.barkSound
    }
    barkSounds.foreach(sound => println(s"A sound: $sound"))
  }

  def runTypeClassScenario: Unit = {
    import com.cory.playground.Addable.add

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