package com.nu.art.pipeline.modules.build

import com.nu.art.pipeline.workflow.Workflow
import com.nu.art.pipeline.workflow.logs.Logger
import com.nu.art.pipeline.workflow.variables.Var_Env
import org.jenkinsci.plugins.workflow.support.steps.build.RunWrapper

class JobTrigger
  extends Logger
  implements Serializable {

  String name
  Workflow workflow
  def params = []
  boolean wait = true
  private RunWrapper triggeredJob

  JobTrigger(Workflow workflow, String JobName) {
    super()
    this.name = JobName
    this.workflow = workflow
  }

  JobTrigger addString(String key, String value) {
    return this.addParam(JobParam.Param_String, key.toString(), value)
  }

  JobTrigger addString(Var_Env envVar, String fallbackValue = null) {
    return this.addParam(JobParam.Param_String, envVar.varName, envVar.get(fallbackValue))
  }

  JobTrigger addBoolean(String key, boolean value = false) {
    return this.addParam(JobParam.Param_Boolean, key.toString(), value)
  }

  JobTrigger addBoolean(Var_Env envVar, boolean fallbackValue = false) {
    def value = envVar.get()
    return this.addParam(JobParam.Param_Boolean, envVar.varName, value ? value.toBoolean() : fallbackValue)
  }

  JobTrigger setWait(boolean wait) {
    this.wait = wait
    return this
  }

  private <T> JobTrigger addParam(JobParam<T> type, String key, T value) {
    params += [$class: type.key, name: key, value: value]
    return this
  }

  RunWrapper run() {
    logInfo("=".repeat(80))
    logInfo("Triggering Job: ${name}")
    logInfo("Wait for completion: ${wait}")
    logInfo("Parameters (${params.size()}):")
    params.each { param ->
      logInfo("  - ${param.name} (${param['$class']}): ${param.value}")
    }
    logInfo("=".repeat(80))

    return this.triggeredJob = workflow.script.build job: name, parameters: params, wait: wait
  }

  String getResultValue(String key) {
    return this.triggeredJob.getBuildVariables()[key]
  }
}
