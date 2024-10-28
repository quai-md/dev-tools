package com.nu.art.pipeline.workflow.variables

class Script {
  String script
  Boolean sandbox = true

  Script(String script, Boolean sandbox = true) {
    this.sandbox = sandbox
    this.script = script
  }
}

class JobParam {
  String type
  String description
  String defaultValue
  Script script

  JobParam(String type, String description = "", String defaultValue = null, Script script = new Script("")) {
    this.type = type
    this.description = description
    this.defaultValue = defaultValue
    this.script = script
  }
}
