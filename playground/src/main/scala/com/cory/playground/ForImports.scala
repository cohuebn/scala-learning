package com.cory.playground

object ForImports {
  implicit object ImprovedIntAdder extends Addable[Int] {
    override def add(val1: Int, val2: Int): Int = {
      val1 + val2 * 10
    }
  }
}
