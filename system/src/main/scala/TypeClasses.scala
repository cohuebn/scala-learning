package com.cory.system

trait Addable[T] {
  def add(val1: T, val2: T): T
}

object Addable {
  implicit object IntAddable extends Addable[Int] {
    override def add(val1: Int, val2: Int): Int = { val1 + val2 }
  }

  implicit object StringAddable extends Addable[String] {
    override def add(val1: String, val2: String): String = { val1.concat(" ").concat(val2) }
  }
}