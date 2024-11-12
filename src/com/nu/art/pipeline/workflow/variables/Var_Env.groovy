package com.nu.art.pipeline.workflow.variables

import com.nu.art.pipeline.exceptions.BadImplementationException
import com.nu.art.pipeline.exceptions.MUSTNeverHappenException
import com.nu.art.pipeline.interfaces.Getter
import com.nu.art.pipeline.workflow.Workflow

class Var_Env
  implements Getter<String> {

  final String varName
  final Getter<String> value
  final JobParam param
  private Var_Env fallbackParam

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
    return this.get(null)
  }

  String getBoolean() {
    return this.get(null).toBoolean()
  }

  String get(String fallbackValue) {
    def value = this.value.get()
    if (this.fallbackParam && (value == null || value == ""))
      return this.fallbackParam.get()

    return value ?: fallbackValue
  }

  String set(String newValue) {
    String oldValue = this.value.get()
    Workflow.workflow.setEnvironmentVariable(this.varName, newValue)
    return oldValue
  }

  Var_Env setFallback(Var_Env fallbackParam) {
    if (this == fallbackParam || fallbackParam.fallbackParam == this)
      throw new BadImplementationException("setting same param instance as fallback")

    this.fallbackParam = fallbackParam
  }

  String assertExist() {
    def varValue = value.get()

    if (varValue == null || varValue.isEmpty())
      throw new MUSTNeverHappenException("${varName} is required but not provided.")

    return varValue
  }
}
