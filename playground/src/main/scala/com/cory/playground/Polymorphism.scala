package com.cory.playground

abstract class BaseClass {
  val name: String

  def printName(): Unit = {
    println(name)
  }
}

class Subclass1(val barkSound: String) extends BaseClass {
  val name = "subclass1"

  def iBark(): Unit = println("Bark!")
}

class Subclass2 extends BaseClass {
  val name = "subclass2"

  def iMoo(): Unit = println("Moo")
}

object BaseClassProcessor {
  def printAndMap[T <: BaseClass, U](items: Traversable[T])(action: T => U) = {
    items.map(item => {
      println(s"Within map all: ${item.name}")
      action(item)
    })
  }
}