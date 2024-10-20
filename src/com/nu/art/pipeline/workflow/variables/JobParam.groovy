package com.nu.art.pipeline.workflow.variables

class JobParam {
  String type
  String description
  String defaultValue

  JobParam(String type) {
    this(type, "", "")
  }

  JobParam(String type, String description, String defaultValue) {
    this.type = type
    this.description = description
    this.defaultValue = defaultValue
  }
}
