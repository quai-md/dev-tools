package com.nu.art.pipeline.workflow.variables

class JobParam {
  String type
  String description
  String defaultValue
  String script

  JobParam(String type) {
    this(type, "", "")
  }

  JobParam(String type, String description, String defaultValue) {
    this(type, description, defaultValue, "")
  }

  JobParam(String type, String description, String defaultValue, String script) {
    this.type = type
    this.description = description
    this.defaultValue = defaultValue
    this.script = script
  }
}
