package com.nu.art.pipeline.modules.curl

import com.nu.art.modular.core.ModuleManager
import com.nu.art.pipeline.exceptions.BadImplementationException
import com.nu.art.pipeline.modules.build.BuildModule
import groovy.json.JsonSlurper

class CURL_Response {
  String pathToResponseFile
  HashMap<String, String> headers = [:]
  Number responseCode

  CURL_Response(String responseFile, HashMap<String, String> headers, Number responseCode) {
    this.pathToResponseFile = responseFile
    this.headers = headers
    this.responseCode = responseCode
  }

  def <T> T json(Closure<T> converter) {
    String response = text()
    return converter(new JsonSlurper().parseText(response))
  }

  String text() {
    if (responseCode != "200")
      throw new BadImplementationException("Error, did not assert for response code.. got ${responseCode}")

    return ModuleManager.ModuleManager.getModule(BuildModule.class).readFromFile(this.pathToResponseFile)
  }
}
