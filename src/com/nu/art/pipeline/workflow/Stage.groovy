package com.nu.art.pipeline.workflow

class Stage {
  String name
  Closure toRun
  Boolean skip

  Stage(String name, Closure toRun) {
    this.name = name
    this.toRun = toRun
  }

  void forceSkip() {
    this.skip = true
  }
}
