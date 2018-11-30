package com.cory.playground

object Variance {
  sealed trait Rsvp {
    val answer: Boolean
  }

  class ProperConfirmation(yesOrNo: Boolean) extends Rsvp {
    override val answer: Boolean = yesOrNo
  }

  class LazyConfirmation(yesOrNo: String) extends Rsvp {
    override val answer: Boolean = yesOrNo.equalsIgnoreCase("yes")
  }

  class Rsvps[+T <: Rsvp](val rsvps: T*) {
    def rowdyParty: Boolean = {
      rsvps.count(_.answer) > 2
    }
  }

  object Rsvps {
    def combine(rsvpSet1: Rsvps[Rsvp], rsvpSet2: Rsvps[Rsvp]): Rsvps[Rsvp] = {
      val combined = rsvpSet1.rsvps ++ rsvpSet2.rsvps
      new Rsvps[Rsvp](combined: _*)
    }
  }
}
