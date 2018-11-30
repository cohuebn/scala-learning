package com.cory.playground

object Variance {
  sealed trait Rsvp {
    val answer: Boolean
  }

  class ProperConfirmation(yesOrNo: Boolean) extends Rsvp {
    override val answer: Boolean = yesOrNo
    val isProper: Boolean = true
  }

  class LazyConfirmation(yesOrNo: String) extends Rsvp {
    override val answer: Boolean = yesOrNo.equalsIgnoreCase("yes")
  }

  class Rsvps[+T <: Rsvp](val rsvps: T*) extends Traversable[T] {
    def rowdyParty: Boolean = {
      rsvps.count(_.answer) > 2
    }

    override def foreach[U](f: T => U): Unit = rsvps.foreach(f)
  }

  object Rsvps {
    def combine(rsvpSet1: Rsvps[Rsvp], rsvpSet2: Rsvps[Rsvp]): Rsvps[Rsvp] = {
      val combined = rsvpSet1.rsvps ++ rsvpSet2.rsvps
      new Rsvps[Rsvp](combined: _*)
    }
  }
}
