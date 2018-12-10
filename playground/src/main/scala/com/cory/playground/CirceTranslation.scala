package com.cory.playground

import io.circe.{Decoder, Json, KeyDecoder}

object CirceTranslation {
  type Examples = Map[String, Json]


  implicit val examplesDecoder: Decoder[Examples] = Decoder.instance[Examples] { cursor =>
    Decoder.decodeMap[String, Json](KeyDecoder.decodeKeyString, Decoder[Json]).tryDecode(cursor)
  }
}
