package com.cory.system

object Playground extends App {
  Tests.runTypeClassScenario
}

object Tests {
  def runPolymorphismScenario(): Traversable[Unit] = {
    val baseClassInstances = List(new Subclass1, new Subclass2)

    BaseClassProcessor.mapAll(baseClassInstances) {
      baseClassInstance => baseClassInstance.printName()
    }

    val barkerInstances = List(new Subclass1, new Subclass1)
    BaseClassProcessor.mapAll(barkerInstances) {
      barkerInstance => barkerInstance.iBark()
    }
  }

  def runTypeClassScenario: Unit = {
    def add[T](vals: Traversable[T])(implicit ev: Addable[T]): T = {
      vals.reduce(ev.add)
    }

    implicit object improvedStringAdder extends Addable[String] {
      override def add(val1: String, val2: String): String = {
        val1.concat("! ").concat(val2)
      }
    }

    val ints = List(1, 2, 3)
    println(s"Added ints: ${add(ints)}")

    val strings = List("this", "is", "a", "sentence")
    println(s"Added strings: ${add(strings)}")
  }
}