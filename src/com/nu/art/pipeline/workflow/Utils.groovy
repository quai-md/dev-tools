package com.nu.art.pipeline.workflow

import com.nu.art.pipeline.workflow.variables.Var_Env

class Utils {
  static boolean isDryRun() {
    return new Var_Env("DRY_RUN").getBoolean("false")
  }
}
