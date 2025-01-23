package com.nu.art.pipeline.workflow.variables


class JobParam {
  public static Type_String = "string"
  public static Type_Choice = "choice"
  public static Type_Boolean = "boolean"

  String type
  String description
  String defaultValue
  JobParamScript script

  JobParam(String type, String description = "", String defaultValue = null, JobParamScript script = new JobParamScript("")) {
    this.type = type
    this.description = description
    this.defaultValue = defaultValue
    this.script = script
  }
}
