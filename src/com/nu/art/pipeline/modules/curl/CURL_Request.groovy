package com.nu.art.pipeline.modules.curl

import com.nu.art.modular.core.ModuleManager


class CURL_Request {

  final String method
  final String url
  final HashMap<String, String> headers = [:]
  Object body

  CURL_Request(String url, String method = "GET") {
    this.method = method
    this.url = url
  }

  CURL_Request setHeaders(HashMap<String, String> headers) {
    this.headers.putAll(headers)
    return this
  }

  CURL_Request setHeader(String key, String value) {
    this.headers.put(key, value)
    return this
  }

  CURL_Request setBody(Object body) {
    this.body = body
    return this
  }

  CURL_Response execute(String outputFile = null) {
    return ModuleManager.ModuleManager.getModule(Module_CURL.class).execute(this, outputFile)
  }

  String compose(String outputFile = null, String bodyFile = null) {
    return ModuleManager.ModuleManager.getModule(Module_CURL.class).composeCommand(this, outputFile, bodyFile)
  }
}

