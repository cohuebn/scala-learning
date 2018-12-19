package com.cory.playground

import shapeless.labelled.FieldType
import shapeless.{::, Generic, HList, HNil, LabelledGeneric, Lazy, Witness}

object Shapeless {
  def playScenario = {
    val original = 1 :: "Yep" :: HNil
    val withExtra = true :: original :: 123 :: HNil
    println(s"The hlist: $withExtra")
  }

  trait MyJsonEncoder[A] {
    def encode(value: A): String
  }

  implicit val stringEncoder: MyJsonEncoder[String] = (value: String) => wrapInQuotes(value)
  implicit val intEncoder = toStringEncoder[Int]
  implicit val boolEncoder = toStringEncoder[Boolean]

  implicit def createArrayEncoder[T](implicit tEncoder: MyJsonEncoder[T]) : MyJsonEncoder[Array[T]] = {
    value: Array[T] => { value.map(tEncoder.encode).mkString("[", ", ", "]") }
  }

  implicit def createMapEncoder[KeyType, ValueType](implicit keyEncoder: MyJsonEncoder[KeyType],
                                                    valueEncoder: MyJsonEncoder[ValueType]): MyJsonEncoder[Map[KeyType, ValueType]] = {
    value: Map[KeyType, ValueType] => {
      val encodedPairs = value.map {
        case (key, value) => {
          s"${keyEncoder.encode(key)}: ${valueEncoder.encode(value)}"
        }
      }
      encodedPairs.mkString("{ ", ", ", " }")
    }
  }

  implicit def hNilEncoder: MyJsonEncoder[HNil] = { _: HNil => "" }
  implicit def hListEncoder[HeadType, TailType <: HList](implicit headEncoder: MyJsonEncoder[HeadType],
                                                         tailEncoder: MyJsonEncoder[TailType]): MyJsonEncoder[HeadType :: TailType] = {
    (value: HeadType :: TailType) => value match {
      case head :: tail => headEncoder.encode(head) + tailEncoder.encode(tail)
    }
  }

  implicit def adtEncoder[AdtType, ListType]
    (implicit genericizer: LabelledGeneric.Aux[AdtType, ListType],
     encoder: Lazy[MyJsonEncoder[ListType]])= {
    new MyJsonEncoder[AdtType] {
      override def encode(value: AdtType): String = {
        val asGeneric = genericizer.to(value)
        val trailingComma = """[,\s]+$"""
        s"{ ${encoder.value.encode(asGeneric).replaceAll(trailingComma, "")} }"
      }
    }
  }

  implicit def createLabelledGenericEncoder[HeadKeyType <: Symbol, HeadValueType, TailType <: HList](
   implicit witness: Witness.Aux[HeadKeyType],
    headEncoder: Lazy[MyJsonEncoder[HeadValueType]],
    tailEncoder: MyJsonEncoder[TailType])
    : MyJsonEncoder[FieldType[HeadKeyType, HeadValueType] :: TailType] = {
    (value: FieldType[HeadKeyType, HeadValueType] :: TailType) => value match {
      case head :: tail => {
        val fieldName = wrapInQuotes(witness.value.name)
        val encodedHead = headEncoder.value.encode(head)
        s"$fieldName: $encodedHead, " + tailEncoder.encode(tail)
      }
    }
  }

  def writeJson[T](value: T)(implicit encoder: MyJsonEncoder[T]): String = {
    encoder.encode(value)
  }

  private def toStringEncoder[T] = {
    new MyJsonEncoder[T] {
      override def encode(value: T): String = value.toString
    }
  }

  private def wrapInQuotes(value: String) = {
    "\"" + value + "\""
  }
}
