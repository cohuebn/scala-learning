package com.cory.playground

import java.text.SimpleDateFormat
import java.util.Date

import com.cory.playground.Variance.LazyConfirmation

object Playground extends App {
  Streams.runBackpressuredStream
}

object Tests {
  def runImplicitConversionScenario = {
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

    import ForImports.ImprovedIntAdder
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

  def runVarianceScenario: Unit = {
    import com.cory.playground.Variance.{ProperConfirmation, Rsvp, Rsvps}

    val properConfirmations = new Rsvps(new ProperConfirmation(true), new ProperConfirmation(false))
    // Still 'proper' type
    properConfirmations.forall(_.isProper)

    // No longer 'proper' type
    val confirmations: Rsvps[Rsvp] = properConfirmations
    println(s"Rowdy party? ${confirmations.rowdyParty}")
    val lateConfirmations = new Rsvps(new LazyConfirmation("yes"), new LazyConfirmation("YES"))
    val updatedConfirmations = Rsvps.combine(confirmations, lateConfirmations)
    println(s"Rowdy party now? ${updatedConfirmations.rowdyParty}")
  }

  def runStreamScenario: Unit = {
    import akka.actor.{ActorSystem, PoisonPill}
    import akka.stream.scaladsl.{Keep, Sink, Source}
    import akka.stream.{ActorMaterializer, OverflowStrategy}

    import scala.concurrent.ExecutionContext.Implicits.global
    import scala.concurrent.duration._

    implicit val system = ActorSystem("LearningToStream")
    implicit val materializer: ActorMaterializer = ActorMaterializer()
    val (actor, actorSource) = Source.actorRef[String](256, OverflowStrategy.fail).preMaterialize()
    val seqSink = Sink.seq[String]
    val allAtOnce = actorSource.toMat(seqSink)(Keep.right).run()
    val streamingSink = Sink.foreach[String](x => println(s"Streaming $x"))
    actorSource.toMat(streamingSink)(Keep.none).run()

    system.scheduler.schedule(0 second, 1 second) {
      val current = now
      actor ! current
    }
    system.scheduler.scheduleOnce(10 seconds) {
      actor ! PoisonPill
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