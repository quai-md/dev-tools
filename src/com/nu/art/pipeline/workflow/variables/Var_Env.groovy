package com.nu.art.pipeline.workflow.variables

import com.nu.art.pipeline.interfaces.Getter
import com.nu.art.pipeline.workflow.Workflow

class Var_Env
  implements Getter<String> {

  final String varName
  final Getter<String> value
  final String type
  String description
  String defaultValue

  static Var_Env create(String varName) {
    return new Var_Env(varName)
  }

  static Var_Env create(String varName, Getter<String> value) {
    return new Var_Env(varName, value)
  }


  Var_Env(String varName) {
    this(varName, "string")
  }

  Var_Env(String varName, String type) {
    this(varName, { Workflow.workflow.getEnvironmentVariable(varName) }, type)
  }

  Var_Env(String varName, Getter<String> value) {
    this(varName, value, "string")
  }

  Var_Env(String varName, Getter<String> value, String type) {
    this.varName = varName
    this.value = value
    this.type = type
  }

  Var_Env setDescription(String description) {
    this.description = description
    return this
  }

  Var_Env setDefaultValue(String defaultValue) {
    this.defaultValue = defaultValue
    return this
  }

  String get() {
    return value.get()
  }

  String set(String newValue) {
    String oldValue = this.value.get()
    Workflow.workflow.setEnvironmentVariable(this.varName, newValue)
    return oldValue
  }
}
