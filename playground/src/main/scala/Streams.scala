package com.cory.playground

import akka.NotUsed
import akka.stream.scaladsl.Source

object Streams {
  def range(max: Int): Source[Int, NotUsed] = {
    Source(1 to max)
  }

  def addFive(source: Source[Int, NotUsed]): Source[Int, NotUsed] = {
    source.map(_ + 5)
  }
}
