package com.nu.art.pipeline.tests

class TestSuite {

  TestSuite(String name, TestCase[] testCases) {
    this.name = name
    this.testCases = testCases
  }

  String name
  TestCase[] testCases
}
