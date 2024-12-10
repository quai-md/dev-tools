package com.nu.art.pipeline.workflow.variables


class Var_CredsFile
  extends Var_Creds {

  Var_CredsFile(String type, String id, Var_Env envVar) {
    super(type, id, envVar)
  }

  Object toCredential(def script) {
    return script."$type"(credentialsId: id, keyFileVariable: envVar.varName)
  }
}
