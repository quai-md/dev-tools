## Required Plugins for Jenkins

* AnsiColor
* Copy Artifact Plugin
* Generic Webhook Trigger Plugin
* Hidden Parameter plugin
* Pipeline Utility Steps
* SSH Agent Plugin

## Idea Libs

* com.nu-art-software:nu-art-core:1.2.59
* com.nu-art-software:belog:1.2.59
* com.nu-art-software:reflection:1.2.59
* com.nu-art-software:module-manager:1.2.59
* org.jenkins-ci.main:jenkins-core:1.538
* org.jenkins-ci.plugins:git:4.4.1
* org.jenkins-ci.plugins:pipeline-utility-steps:2.6.1
* org.jenkins-ci.plugins:scm-api:2.6.4
* org.jenkins-ci.plugins.workflow:workflow-api:2.40
* org.jenkins-ci.plugins.workflow:workflow-cps:2.83
* org.jenkins-ci.plugins.workflow:workflow-job:2.40
* org.jenkins-ci.plugins.workflow:workflow-scm-step:2.6
* org.jenkins-ci.plugins:ssh-agent:295.v9ca_a_1c7cc3a_a_

## Agent rules

Pipeline authors and agents: load [`.cursor/rules/jenkins-api-isolation.mdc`](../../../../.cursor/rules/jenkins-api-isolation.mdc) — Jenkins DSL/API calls must be wrapped in `WorkflowModule` classes; consumer pipelines use `BuildModule`, `SlackModule`, etc., never raw `workflow.script`.

## BuildModule (consumer API)

| Method | Purpose |
|---|---|
| `setDisplayName` / `getDisplayName` | Build title in Jenkins UI |
| `setDescription` / `getDescription` | Build description |
| `findFiles(glob)` | Workspace file discovery |
| `archiveArtifacts(pattern, onlyIfSuccessful, allowEmptyArchive)` | Attach files to build (Build → Artifacts) |
| `copyArtifacts(job, build)` | Returns `CopyArtifacts` helper |
| `triggerJob(name)` | Returns `JobTrigger` |

## Maven Repo for Jenkins

* https://repo.jenkins-ci.org/releases