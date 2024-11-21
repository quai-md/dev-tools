package com.nu.art.pipeline.modules.firebase

import com.nu.art.pipeline.workflow.WorkflowModule
import groovy.json.JsonOutput
import groovy.json.JsonSlurper

class FirebaseDatabaseModule
  extends WorkflowModule {
  private String prefix = """
         . \$HOME/.nvm/nvm.sh
         nvm use 18.15.0
      """

  String defaultProjectId
  boolean installViaNVM = false

  @Override
  void _init() {
  }

  void install() {
    if (installViaNVM)
      bash("""
         curl -o- \"https://raw.githubusercontent.com/nvm-sh/nvm/v0.40.1/install.sh\" | bash
         echo ------------------------------ source
         . \$HOME/.nvm/nvm.sh
         echo ------------------------------ install
         nvm install 18.15.0
         echo ------------------------------ use
         nvm use 18.15.0 
         echo ------------------------------ npm install
         npm install -g firebase-tools
        """)
  }

  void setDefaultProjectId(String defaultProjectId) {
    this.defaultProjectId = defaultProjectId
  }

  private void setValue(String path, String value, String projectId = this.defaultProjectId, String databaseUrl = null) {
    try {
      databaseUrl = databaseUrl ?: "${projectId}-default-rtdb"
      String instance = databaseUrl ? " --instance=${databaseUrl}" : ""

      def command = "firebase database:set ${path} --data '${value}' --project ${projectId} --force ${instance}"
      this.logDebug("'${command}'")
      bash("""
           ${installViaNVM ? prefix : ""}
           ${command}
        """)
    } catch (Throwable t) {
      this.logWarning("Failed to write value to RTDB: ", t)
      throw t
    }
  }

  // Get a value from RTDB as a String
  private String getValue(String path, String projectId = this.defaultProjectId, String databaseUrl = null) {
    try {
      databaseUrl = databaseUrl ?: "${projectId}-default-rtdb"
      String instance = databaseUrl ? " --instance=${databaseUrl}" : ""
      def command = "firebase database:get ${path} --project ${projectId} ${instance}"
      this.logDebug("'${command}'")

      def result = bash("""
          ${installViaNVM ? prefix : ""}
          ${command}
      """, true).trim()

      if (result == "null" || result.isEmpty())
        return null

      return result
    } catch (Throwable t) {
      this.logWarning("Failed to fetch value from RTDB: ", t)
      throw t
    }
  }


  void setString(String path, String value, String projectId = this.defaultProjectId, String databaseUrl = null) {
    this.setValue(path, "\"${value}\"", projectId, databaseUrl)
  }

  void setNumber(String path, Number value, String projectId = this.defaultProjectId, String databaseUrl = null) {
    this.setValue(path, "${value}", projectId, databaseUrl)
  }

// Set a value in RTDB
  def <T> void setObj(String path, T value, String projectId = this.defaultProjectId, String databaseUrl = null) {
    String valueJson = JsonOutput.toJson(value)
    this.setValue(path, "${valueJson}", projectId, databaseUrl)
  }


// Get a value from RTDB as a String
  String getString(String path, String defaultValue = null, String projectId = this.defaultProjectId, String databaseUrl = null) {

    String value = this.getValue(path, projectId, databaseUrl)
    if (value == null)
      return defaultValue

    return new JsonSlurper().parseText(value)
  }

// Get a value from RTDB as an Integer
  Number getNumber(String path, Integer defaultValue, String projectId = this.defaultProjectId, String databaseUrl = null) {
    String output = this.getValue(path, projectId, databaseUrl)
    if (output == null)
      return defaultValue

    return output.toInteger()
  }

// Generic method to get a value from RTDB
  def <T> T getObj(String path, T defaultValue, String projectId = this.defaultProjectId, String databaseUrl = null) {
    String output = this.getValue(path, projectId, databaseUrl)
    if (output == null)
      return defaultValue

    return (T) new JsonSlurper().parseText(output)
  }
}







