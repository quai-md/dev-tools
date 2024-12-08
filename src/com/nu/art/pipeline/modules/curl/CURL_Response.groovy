package com.nu.art.pipeline.modules.curl

class CURL_Response {
  String pathToResponseFile
  HashMap<String, String> headers = [:]
  Number responseCode

  CURL_Response(String responseFile, HashMap<String, String> headers, Number responseCode) {
    this.pathToResponseFile = responseFile
    this.headers = headers
    this.responseCode = responseCode
  }
}
