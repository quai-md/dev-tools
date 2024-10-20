package com.nu.art.pipeline.workflow.variables

import com.nu.art.pipeline.interfaces.Getter
import com.nu.art.pipeline.workflow.Workflow

class Var_Env
  implements Getter<String> {

  final String varName
  final Getter<String> value
  final JobParam param

  static Var_Env create(String varName) {
    return new Var_Env(varName)
  }

  static Var_Env create(String varName, Getter<String> value) {
    return new Var_Env(varName, value)
  }


  Var_Env(String varName) {
    this(varName, new JobParam("string"))
  }

  Var_Env(String varName, JobParam param) {
    this(varName, { Workflow.workflow.getEnvironmentVariable(varName) }, param)
  }

  Var_Env(String varName, Getter<String> value) {
    this(varName, value, new JobParam("string"))
  }

  Var_Env(String varName, Getter<String> value, JobParam param) {
    this.varName = varName
    this.value = value
    this.param = param
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
