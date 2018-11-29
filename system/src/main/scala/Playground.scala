package com.cory.system

object Playground extends App {
  val baseClassInstances = List(new Subclass1, new Subclass2)

  BaseClassProcessor.mapAll(baseClassInstances) {
    baseClassInstance => baseClassInstance.printName()
  }

  val barkerInstances = List(new Subclass1, new Subclass1)
  BaseClassProcessor.mapAll(barkerInstances) {
    barkerInstance => barkerInstance.iBark()
  }

//  val strings = List("blah", "yah")
//  BaseClassProcessor.mapAll(strings) {
//    string => string
//  }
}