package com.nu.art.pipeline

import com.nu.art.exception

class GitRepo {

  final MyPipeline pipeline
  final String url

  String service = "GithubWeb"
  String branch = "master"
  String folderName = ""
  String shallowClone = false
  Boolean changelog = true

  GitRepo(MyPipeline pipeline, String url) {
    this.pipeline = pipeline
    this.url = url
    this.folderName = url.replace(".git", "").substring(url.lastIndexOf("/") + 1)
  }

  void cloneRepo() {
    String url = this.url.replace(".git", "")
    pipeline.script.checkout changelog: changelog,
      scm: [
        $class           : 'GitSCM',
        branches         : [[name: branch]],
        timeout          : 30,
        extensions       : [[$class: 'LocalBranch', localBranch: "**"],
                            [$class             : 'SubmoduleOption',
                             disableSubmodules  : true,
                             parentCredentials  : true,
                             recursiveSubmodules: true,
                             reference          : '',
                             trackingSubmodules : false],
                            [$class: 'CloneOption', noTags: false, reference: '', shallow: shallowClone],
                            [$class: 'CheckoutOption'],
                            [$class: 'RelativeTargetDirectory', relativeTargetDir: folderName]],
        browser          : [$class: "${service}", repoUrl: url],
        userRemoteConfigs: [[url: url + '.git']]
      ]

    Closure updateSubmodules = {
      pipeline.sh "git submodule update --recursive --init"
    }


    if (folderName != "")
      pipeline.cd(folderName, updateSubmodules)
    else
      updateSubmodules.call()
  }

  GitRepo setService(String service) {
    this.service = service
    return this
  }

  GitRepo setBranch(String branch) {
    this.branch = branch
    return this
  }

  GitRepo setFolderName(String folderName) {
    this.folderName = folderName
    return this
  }

  GitRepo setShallowClone(String shallowClone) {
    this.shallowClone = shallowClone
    return this
  }

  GitRepo setChangelog(Boolean changelog) {
    this.changelog = changelog
    return this
  }

  void createTag(String tagName) {
    if (!tagName)
      throw new exception.BadImplementationException("tag name is undefined")

    pipeline.sh("git tag -f ${tagName}")
  }

  void pushTags() {
    pipeline.sh("git push --tags")
  }

  void push() {
    pipeline.sh("git push")
  }

  void commit(String message) {
    pipeline.sh("git commit -am \"${message}\"")
  }
}

