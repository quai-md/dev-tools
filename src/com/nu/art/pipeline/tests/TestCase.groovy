package com.nu.art.pipeline.tests

class TestCase<T> {

  String name
  Closure<T> test
  T expectedResults

  TestCase(String name, T expectedResults, Closure<T> test) {
    this.name = name
    this.test = test
    this.expectedResults = expectedResults
  }
}
