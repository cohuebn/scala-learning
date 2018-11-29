package com.cory.playground

import java.text.SimpleDateFormat
import java.util.{Calendar, Date}

object Playground extends App {
  Tests.runImplicitConversionTest
}

object Tests {
  def runImplicitConversionTest = {
    import com.cory.playground.BooleanConverter.toBoolean

    def iPrintBooleans(value: Boolean): Unit = {
      println(s"I print booleans: $value")
    }

    iPrintBooleans("YeS")
    iPrintBooleans("no")
    if ("Yes")
      println("Crazy conversion works!")
  }

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
    import scala.concurrent.duration._
    import akka.actor.ActorSystem
    import akka.actor.PoisonPill

    import akka.stream.scaladsl.{Keep, Sink, Source}
    import akka.stream.{ActorMaterializer, OverflowStrategy}

    implicit val system = ActorSystem("LearningToStream")
    implicit val materializer: ActorMaterializer = ActorMaterializer()

    val actorSource = Source.actorRef[String](5, OverflowStrategy.fail)
    val seqSink = Sink.seq[String]
    val streamingSink = Sink.foreach[String](x => println(s"Streaming $x"))
    val (actor, allAtOnce) = actorSource.toMat(seqSink)(Keep.both).run()
    val (actor2) = actorSource.toMat(streamingSink)(Keep.left).run()

    system.scheduler.schedule(0 second, 1 second) {
      val current = now
      actor ! current
      actor2 ! current
    }
    system.scheduler.scheduleOnce(10 seconds) {
      actor ! PoisonPill
      actor2 ! PoisonPill
      system.terminate()
    }

    // Streaming vs. all-at-once
    allAtOnce.foreach(x => println(s"Sequence: $x"))
  }

  def now: String = {
    val date = new Date
    val formatter = new SimpleDateFormat("hh:mm:ss")
    formatter.format(date)
  }
}