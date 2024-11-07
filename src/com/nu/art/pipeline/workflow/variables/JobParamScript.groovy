package com.nu.art.pipeline.workflow.variables

class JobParamScript {
  String script
  Boolean sandbox = true

  JobParamScript(String script, Boolean sandbox = true) {
    this.sandbox = sandbox
    this.script = script
  }
}
