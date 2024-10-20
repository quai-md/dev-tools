package com.nu.art.pipeline.workflow

class Stage {
  String name
  Closure toRun

  Stage(String name, Closure toRun) {
    this.name = name
    this.toRun = toRun
  }
}
