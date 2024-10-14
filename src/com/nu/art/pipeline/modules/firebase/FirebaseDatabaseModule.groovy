package com.nu.art.pipeline.modules.firebase

import com.nu.art.pipeline.workflow.WorkflowModule
import groovy.json.JsonOutput
import groovy.json.JsonSlurper

class FirebaseDatabaseModule
  extends WorkflowModule {

  String defaultProjectId
  String defaultDatabaseUrl

  @Override
  void _init() {
  }

  void install() {
    sh("""
         curl -o- \"https://raw.githubusercontent.com/nvm-sh/nvm/v0.40.1/install.sh\" | bash
         echo ------------------------------ source
         . \$HOME/.nvm/nvm.sh > /dev/null 2>&1
         echo ------------------------------ install
         nvm install 18.15.0 > /dev/null 2>&1
         echo ------------------------------ use
         nvm use 18.15.0  > /dev/null 2>&1
         echo ------------------------------ npm install
         npm install -g firebase-tools
        """)
  }

  void setDefaultDatabaseUrl(String defaultDatabaseUrl) {
    this.defaultDatabaseUrl = defaultDatabaseUrl
  }

  void setDefaultProjectId(String defaultProjectId) {
    this.defaultProjectId = defaultProjectId
  }

  private void setValue(String path, String value, String projectId = this.defaultProjectId, String databaseUrl = this.defaultDatabaseUrl) {
    sh("""
         . \$HOME/.nvm/nvm.sh > /dev/null 2>&1
         nvm use 18.15.0 > /dev/null 2>&1
          
          firebase database:set ${path} ${value} --data '${valueJson} --project ${projectId} --database-url=${databaseUrl}
      """)
  }

  // Get a value from RTDB as a String
  private String getValue(String path, String projectId = this.defaultProjectId, String databaseUrl = this.defaultDatabaseUrl) {
    try {
      def result = sh("""
         . \$HOME/.nvm/nvm.sh > /dev/null 2>&1
         nvm use 18.15.0  > /dev/null 2>&1

        firebase database:get ${path} --project ${projectId} --database-url=${databaseUrl}
      """, true).trim()
      if (result == "null" || result.isEmpty())
        return null

      return result
    } catch (Throwable t) {
      this.logWarning("Failed to fetch value from RTDB: ", t)
      throw t
    }
  }


  void setString(String path, String value, String projectId = this.defaultProjectId, String databaseUrl = this.defaultDatabaseUrl) {
    this.setValue(path, "'${value}'", projectId, databaseUrl)
  }

  void setNumber(String path, Number value, String projectId = this.defaultProjectId, String databaseUrl = this.defaultDatabaseUrl) {
    this.setValue(path, "${value}", projectId, databaseUrl)
  }

// Set a value in RTDB
  def <T> void setObj(String path, T value, String projectId = this.defaultProjectId, String databaseUrl = this.defaultDatabaseUrl) {
    String valueJson = JsonOutput.toJson(value)
    this.setValue(path, "'${valueJson}'", projectId, databaseUrl)
  }


// Get a value from RTDB as a String
  String getString(String path, String defaultValue, String projectId = this.defaultProjectId, String databaseUrl = this.defaultDatabaseUrl) {
    String value = this.getValue(path, projectId, databaseUrl)
    if (value == null)
      return defaultValue

    return value
  }

// Get a value from RTDB as an Integer
  Number getNumber(String path, Integer defaultValue, String projectId = this.defaultProjectId, String databaseUrl = this.defaultDatabaseUrl) {
    String output = this.getValue(path, projectId, databaseUrl)
    if (output == null)
      return defaultValue

    return output.toInteger()
  }

// Generic method to get a value from RTDB
  def <T> T getObj(String path, T defaultValue, String projectId = this.defaultProjectId, String databaseUrl = this.defaultDatabaseUrl) {
    String output = this.getValue(path, projectId, databaseUrl)
    if (output == null)
      return defaultValue

    return (T) new JsonSlurper().parseText(output)
  }
}
