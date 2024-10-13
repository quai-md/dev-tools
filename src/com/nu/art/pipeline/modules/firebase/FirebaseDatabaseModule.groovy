package com.nu.art.pipeline.modules.firebase

import com.nu.art.pipeline.workflow.WorkflowModule
import groovy.json.JsonSlurper

class FirebaseDatabaseModule
  extends WorkflowModule {

  String defaultProjectId
  String defaultDatabaseUrl

  @Override
  void _init() {
  }

  void setDefaultDatabaseUrl(String defaultDatabaseUrl) {
    this.defaultDatabaseUrl = defaultDatabaseUrl
  }

  void setDefaultProjectId(String defaultProjectId) {
    this.defaultProjectId = defaultProjectId
  }

  <T> void setValue(String path, T value, String projectId = this.defaultProjectId, String databaseUrl = this.defaultDatabaseUrl) {
    sh("firebase database:get ${path} --project ${projectId} --database-url=${databaseUrl}", true)
  }

  <T> T getValue(String path, T defaultValue, String projectId = this.defaultProjectId, String databaseUrl = this.defaultDatabaseUrl) {
    String output = sh("firebase database:get ${path} --project ${projectId} --database-url=${databaseUrl}", true).trim()
    this.logWarning(output);
    return (T) new JsonSlurper().parseText(output)
  }

}
