package com.cory.web

import akka.actor.ActorRef
import com.cory.core.GreetingTranslator.GreetingRequest
import com.cory.core.Dialects.dialects

import scala.util.Random

trait GreetingRequestMagnet {
  def apply(): GreetingRequest
}

object GreetingRequestMagnet {
  implicit def fromName(name: String): GreetingRequestMagnet = {
    () => {
      val randomDialect = Random.shuffle(dialects).head
      GreetingRequest(randomDialect, name)
    }
  }

  implicit def fromValidatedRequest(validatedRequest: ValidatedGreetingRequest): GreetingRequestMagnet = {
    () => GreetingRequest(validatedRequest.dialect, validatedRequest.name)
  }
}