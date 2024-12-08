package com.nu.art.pipeline.modules.curl

import com.nu.art.belog.Logger
import com.nu.art.pipeline.tests.TestCase
import com.nu.art.pipeline.tests.TestSuite
import com.nu.art.pipeline.workflow.WorkflowModule

@Grab('com.nu-art-software:belog:1.2.34')


class Tests_CURL
  extends Logger {

  public static Class<? extends WorkflowModule>[] modules = [Module_CURL.class]

  TestSuite collectTests() {
    TestCase testCase1 = new TestCase("Simple", Consts.Test1_Expected, {
      return new CURL_Request("https://my-domain.com/v1/artifacts/check-version-exists")
        .setHeader("Content-Type", "application/json")
        .setHeader("x-application", "my-application")
        .setBody([label: "this is the body"])
        .compose("/tmp/output", "/tmp/input")
    })
    TestCase testCase2 = new TestCase("Simple", Consts.Test2_Expected, {
      return new CURL_Request("https://my-domain.com/v1/artifacts/check-version-exists")
        .setHeader("Content-Type", "application/json")
        .setHeader("x-application", "my-application")
        .compose("/tmp/output", "/tmp/input")
    })

    TestCase[] cases = [
      testCase1,
      testCase2
    ]
    return new TestSuite("CURL Infra", cases)
  }
}

class Consts {
  public static String Test1_Expected = """curl -X GET -H "Content-Type: application/json" -H "x-application: my-application" --data @/tmp/input -o /tmp/output -s -w '%{http_code}' https://my-domain.com/v1/artifacts/check-version-exists"""
  public static String Test2_Expected = """curl -X GET -H "Content-Type: application/json" -H "x-application: my-application" -o /tmp/output -s -w '%{http_code}' https://my-domain.com/v1/artifacts/check-version-exists"""
}
