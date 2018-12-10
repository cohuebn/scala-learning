package com.cory.playground

import io.circe.{Decoder, KeyDecoder}
import io.circe.generic.auto._
import cats.syntax.functor._

sealed trait ExampleField
case class ExampleString(value: String) extends ExampleField
object ExampleString {
  implicit def fromString(value: String): ExampleString = ExampleString(value)
}
case class ExampleInt(value: Int) extends ExampleField
object ExampleInt {
  implicit def fromInt(value: Int): ExampleInt = ExampleInt(value)
}

object CirceTranslation {
  type Examples = Map[String, ExampleField]

  val exampleFieldDecoder = List[Decoder[ExampleField]](
    Decoder[ExampleString].widen,
    Decoder[ExampleInt].widen,
  ).reduceLeft(_ or _)
  implicit val examplesDecoder: Decoder[Examples] = Decoder.instance[Examples] { cursor =>
    Decoder.decodeMap[String, ExampleField](KeyDecoder.decodeKeyString, exampleFieldDecoder).tryDecode(cursor)
  }
}
