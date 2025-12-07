package com.nu.art.pipeline.modules.curl

import com.nu.art.pipeline.exceptions.BadImplementationException
import com.nu.art.pipeline.workflow.WorkflowModule
import com.nu.art.pipeline.workflow.variables.VarConsts
import groovy.json.JsonBuilder

class Module_CURL
	extends WorkflowModule {

	CURL_Response execute(CURL_Request request, String responseFile = null) {
		if (responseFile == null)
			responseFile = "${VarConsts.Var_Workspace.get()}/tmp/curl_response_${System.currentTimeMillis()}.tmp"

		String command = composeCommand(request, responseFile)
		this.logDebug("Executing: ${command}")

		// Execute command
		String result = workflow.sh(command, true).trim()
		Number responseCode = result.isInteger() ? result.toInteger() : 0

		// Parse headers (if needed, extend to capture headers with `-i` or a similar flag)
		HashMap<String, String> responseHeaders = [:]

		return new CURL_Response(responseFile, responseHeaders, responseCode)
	}

	String composeCommand(CURL_Request request, String pathToResponseFile, String pathToBodyFile = "${VarConsts.Var_Workspace.get()}/tmp/curl_body_${System.currentTimeMillis()}.tmp") {
		if (!request.url)
			throw new BadImplementationException("URL is required for the request.")

		// Prepare headers
		String headerOptions = request.headers.collect { key, value -> "-H \"${key}: ${value}\"" }.join(" ")

		// Prepare body
		String bodyOption = ""
		Object body = request.body
		if (body) {
			def finalBody = body instanceof String ? body : new JsonBuilder(body).toString()
			workflow.writeToFile(pathToBodyFile, finalBody)
			bodyOption = " --data @${pathToBodyFile}"
			this.logInfo("finalBody: ${finalBody}")

//			def contentLength = finalBody.getBytes("UTF-8").length
//			headerOptions += " -H \"Content-Length: ${contentLength}\""
		}

		// Construct curl command
		String command = "curl -v -X ${request.method} ${bodyOption} -o ${pathToResponseFile} ${headerOptions} -s -w '%{http_code}' ${request.url}"
		command
	}
}
