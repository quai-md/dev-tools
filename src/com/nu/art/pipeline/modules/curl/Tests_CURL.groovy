package com.nu.art.pipeline.modules.curl

import com.nu.art.belog.Logger
import com.nu.art.pipeline.tests.TestCase
import com.nu.art.pipeline.tests.TestSuite
import com.nu.art.pipeline.workflow.WorkflowModule

class Consts {
  public static String Test1_Expected = """
								curl -X POST \
									-H "Content-Type: application/json" \
									-H "x-application: my-application" \
									-d '{ "label": "this is the body" }' \
									https://my-domain.com/v1/artifacts/check-version-exists
                """
}

class Tests_CURL
  extends Logger {
  public static Class<? extends WorkflowModule>[] modules = [Module_CURL.class]

  TestSuite collectTests() {
    TestCase testCase1 = new TestCase("Simple", Consts.Test1_Expected, {
      return new CURL_Request("https://my-domain.com/v1/artifacts/check-version-exists")
        .setHeader("Content-Type", "application/json")
        .setHeader("x-application", "my-application")
        .setBody([label: "this is the body"])
        .compose("/tmp/output")
    })
    TestCase[] cases = [
      testCase1,
    ]
    return new TestSuite("CURL Infra", cases)
  }
}
