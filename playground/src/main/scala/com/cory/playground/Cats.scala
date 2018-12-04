package com.cory.playground

object Cats {
  def optionCombine[A](a: A, opt: Option[A]): A =
    opt.getOrElse(a)

  def mergeMaps[K, V](lhs: Map[K, V], rhs: Map[K, V]): Map[K, V] =
    lhs.foldLeft(rhs) {
      case (acc, (k, v)) => acc.updated(k, optionCombine(v, acc.get(k)))
    }
}
