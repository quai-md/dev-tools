#!/bin/bash
CONST_TS_VER_JSON="version-thunderstorm.json"
CONST_APP_VER_JSON="version-app.json"
CONST_TS_ENV_FILE=".ts_env"
CONST_Version_Typescript=latest
CONST_Version_ESlint=latest
CONST_Version_FirebaseTools=latest

WorkspaceV3() {

  declare currentThunderstormVersion
  declare thunderstormVersion

  declare currentAppVersion
  declare appVersion

  declare -a tsLibs
  declare -a projectLibs
  declare -a active
  declare -a apps
  declare -a allLibs

  _readConfigProp() {
    local prop=${1}
    local defaultValue=${2}

    [[ ! -e "${CONST_TS_ENV_FILE}" ]] && echo "${defaultValue}" && return 0

    local value=$(cat "${CONST_TS_ENV_FILE}" | grep -E "${prop}=" | sed -E "s/^${prop}=\"(.*)\"$/\1/")
    [[ ! "${value}" ]] && value=${defaultValue}
    echo "${value}"
  }

  _setThunderstormVersion() {
    [[ ! -e "${CONST_TS_VER_JSON}" ]] && throwError "MUST add ${CONST_TS_VER_JSON} to project root" 2
    if [[ ! "${thunderstormVersion}" ]]; then
      thunderstormVersion=$(getVersionName "./${CONST_TS_VER_JSON}")
    fi

    if [[ "${promoteThunderstormVersion}" ]]; then
      currentThunderstormVersion=${thunderstormVersion}
      [[ "${promoteThunderstormVersion}" ]] && thunderstormVersion="$(promoteVersion "${currentThunderstormVersion}" "${promoteThunderstormVersion}")"
      if [[ "${currentThunderstormVersion}" != "${thunderstormVersion}" ]]; then
        logInfo "Promoting thunderstorm: ${currentThunderstormVersion} => ${thunderstormVersion}"
        this.assertRepoForVersionPromotion "${currentThunderstormVersion}"
        setVersionName "${thunderstormVersion}" "./${CONST_TS_VER_JSON}"
      fi
    fi

    THUNDERSTORM_SDK_VERSION="${thunderstormVersion}"
    logInfo "Thunderstorm version: ${THUNDERSTORM_SDK_VERSION}"
  }

  _setAppVersion() {
    [[ ! -e "${CONST_APP_VER_JSON}" ]] && throwError "MUST add ${CONST_APP_VER_JSON} to project root" 2
    if [[ ! "${appVersion}" ]]; then
      appVersion=$(getVersionName "./${CONST_APP_VER_JSON}")
    fi

    if [[ "${promoteAppVersion}" ]]; then
      currentAppVersion=${appVersion}
      [[ "${promoteAppVersion}" ]] && appVersion="$(promoteVersion "${appVersion}" "${promoteAppVersion}")"

      if [[ "${currentAppVersion}" != "${appVersion}" ]]; then
        logInfo "Promoting app version: ${currentAppVersion} => ${appVersion}"
        this.assertRepoForVersionPromotion "${appVersion}"
        setVersionName "${appVersion}" "./${CONST_APP_VER_JSON}"
      fi
    fi

    APP_VERSION="${appVersion}"
    logInfo "App version: ${APP_VERSION}"
  }

  _setWorkspaceFile() {
    file.delete ./pnpm-workspace.yaml

    [[ ! "${ts_linkThunderstorm}" ]] && ts_linkThunderstorm=$(this.readConfigProp ts-sources)

    if [[ "${ts_linkThunderstorm}" == "true" ]]; then
      logInfo "Running with Thunderstorm..."
      file.copy ./.config/pnpm-workspace.ts.yaml . pnpm-workspace.yaml
    else
      file.copy ./.config/pnpm-workspace.yaml . pnpm-workspace.yaml
      ts_linkThunderstorm=
    fi
  }

  _setEnv() {
    local newEnv="${envType}"
    if [[ ! "${newEnv}" ]]; then
      envType=$(this.readConfigProp env local)
      fallbackEnv=$(this.readConfigProp fb-env "")
    fi

    [[ "${envType}" != "local" ]] && compilerFlags+=(--sourceMap false)
    logInfo "Env: ${envType}"
    [[ "${fallbackEnv}" ]] && logWarning " -- Fallback env: ${fallbackEnv}"
  }

  _prepare() {
    this.setThunderstormVersion
    this.setAppVersion
    this.setWorkspaceFile
    this.setEnv

    echo "env=\"${envType}\"" > "${CONST_TS_ENV_FILE}"
    [[ "${fallbackEnv}" ]] && echo "fb-env=\"${fallbackEnv}\"" >> "${CONST_TS_ENV_FILE}"
    [[ "${ts_linkThunderstorm}" ]] && echo "ts-sources=\"${ts_linkThunderstorm}\"" >> "${CONST_TS_ENV_FILE}"

  }

  _assertRepoForVersionPromotion() {
    logDebug "Asserting repo readiness to promote a version..."
    [[ "${noGit}" ]] && return
    [[ $(gitAssertTagExists "v${1}") ]] && throwError "Tag already exists: v${1}" 2

    gitAssertBranch "${allowedBranchesForPromotion[@]}"
    gitFetchRepo
    gitAssertRepoClean
    gitAssertNoCommitsToPull
  }

  WorkspaceV3.active.forEach() {
    this.forEach "${1}" "${active[*]}" "${@:2}"
  }

  WorkspaceV3.apps.forEach() {
    this.forEach "${1}" "${apps[*]}" "${@:2}"
  }

  WorkspaceV3.tsLibs.forEach() {
    this.forEach "${1}" "${tsLibs[*]}" "${@:2}"
  }

  _forEach() {
    local command=${1}
    [[ ! "${command}" ]] && throwError "No command specified" 2
    local items=(${2})
    local p="${startFromPackage}"

    for (( ; p < ${#items[@]}; p++)); do
      item=${items[${p}]}
      startFromPackage=${p}
      saveState

      #      logDebug "$("${item}.folderName")"
      _pushd "$("${item}.path")/$("${item}.folderName")"
      [[ "${item}.${command}" ]] && "${item}.${command}" "${@:3}"
      (($? > 0)) && throwError "Error executing command: ${item}.${command}"
      _popd
    done
    startFromPackage=0
  }

  _printDependencyTree() {
    [[ ! "${ts_dependencies}" ]] && return

    this.active.forEach printDependencyTree
    exit 0
  }

  _cleanEnv() {
    [[ ! "${ts_cleanENV}" ]] && return

    logInfo
    bannerInfo "Clean ENV"

    nvm deactivate
    nvm uninstall "v$(cat .nvmrc | head -1)"

    exit 0
  }

  _prepareToPublish() {
    [[ ! "${ts_publish}" ]] && return

    assertRepoIsClean() {
      logDebug "Asserting main repo readiness to promote a version..."
      gitAssertBranch master staging
      gitAssertRepoClean
      gitFetchRepo
      gitAssertNoCommitsToPull
    }

    gitAssertOrigin "${boilerplateRepo}"
    assertRepoIsClean
  }

  _setEnvironment() {
    bannerInfo "Set Environment"

    copyConfigFile "./.config/firebase-ENV_TYPE.json" "firebase.json" "${envType}" "${fallbackEnv}"
    copyConfigFile "./.config/.firebaserc-ENV_TYPE" ".firebaserc" "${envType}" "${fallbackEnv}"

    local firebaseProject="$(getJsonValueForKey .firebaserc default)"
    [[ "${firebaseProject}" ]] && $(resolveCommand firebase) login
    [[ "${firebaseProject}" ]] && verifyFirebaseProjectIsAccessible "${firebaseProject}"
    [[ "${firebaseProject}" ]] && $(resolveCommand firebase) use "${firebaseProject}"

    this.apps.forEach setEnvironment
  }

  _assertNoCyclicImport() {
    [[ ! "${checkCircularImports}" ]] && return

    logInfo
    bannerInfo "Cyclic Imports"

    this.active.forEach assertNoCyclicImport
  }

  _purge() {
    [[ ! "${ts_purge}" ]] && return

    logInfo
    bannerInfo "Purge"

    file.delete "${Path_RootRunningDir}/.ts_env"
    file.delete "${Path_RootRunningDir}/.pnpm-lock.yaml"
    this.active.forEach purge
  }

  _clean() {
    [[ ! "${ts_clean}" ]] && return

    logInfo
    bannerInfo "Clean"

    this.active.forEach clean
  }

  _generateDocs() {
    [[ ! "${ts_generateDocs}" ]] && return

    logInfo
    bannerInfo "Generating docs"

    this.active.forEach generateDocs
  }

  _installGlobalPackages() {
    if [[ "${ts_installGlobals}" ]]; then
      logInfo "Installing global packages..."
      npm i -g typescript@${CONST_Version_Typescript} eslint@${CONST_Version_ESlint} tslint@latest firebase-tools@${CONST_Version_FirebaseTools} sort-package-json@latest sort-json@latest tsc-watch@latest typedoc@latest
      storeFirebasePath
    fi
  }

  _install() {
    [[ ! "${ts_installPackages}" ]] && return 0
    logInfo
    bannerInfo "Install"
    this.active.forEach install "${allLibs[@]}"

    pnpm.installPackages
  }

  _link() {
    [[ ! "${ts_link}" ]] && return

    logInfo
    bannerInfo "Link"

    #    this.active.forEach link "${allLibs[@]}"
  }

  _compile() {
    [[ ! "${ts_compile}" ]] && return
    logInfo
    bannerInfo "Compile"

    this.active.forEach compile "${allLibs[@]}"

    [[ "${ts_watch}" ]] && deleteFile "${CONST_BuildWatchFile}"
    for lib in "${allLibs[@]}"; do
      local length=$("${lib}.newWatchIds.length")
      ((length == 0)) && continue
      for ((watchId = 0; watchId < length; watchId++)); do
        local var="${lib}_newWatchIds[${watchId}]"
        echo -e "${!var}" >> "${CONST_BuildWatchFile}"
      done
    done
  }

  _lint() {
    [[ ! "${ts_lint}" ]] && return
    logInfo
    bannerInfo "Lint"

    this.active.forEach lint
  }

  _test() {
    [[ ! "${ts_runTests}" ]] && return
    [[ ! "${testServiceAccount}" ]] && throwError "MUST specify path to a test service account" 2

    logInfo
    bannerInfo "Test"
    this.active.forEach test
  }

  _launch() {
    ((${#ts_launch[@]} == 0)) && return

    logInfo
    bannerInfo "Launch"

    this.active.forEach launch
  }

  _deploy() {
    ((${#ts_deploy[@]} == 0)) && return

    logInfo
    bannerInfo "Deploy"

    [[ ! "${envType}" ]] && throwError "MUST set env while deploying!!" 2

    this.apps.forEach deploy

    [[ "${noGit}" ]] && return

    logInfo "Deployed Apps: ${currentAppVersion} => ${appVersion}"
    gitTag "v${currentAppVersion}" "Promoted apps to: v${appVersion}"
    gitPushTags
    throwError "Error pushing promotion tag"

    gitNoConflictsAddCommitPush "Branch" "$(gitGetCurrentBranch)" "published version v${appVersion}"
  }

  _publish() {
    [[ ! "${ts_publish}" ]] && return

    logInfo
    bannerInfo "Publish Thunderstorm"

    this.tsLibs.forEach canPublish
    this.tsLibs.forEach publish

    [[ "${noGit}" ]] && return

    logInfo "Promoted thunderstorm packages: ${currentThunderstormVersion} => ${thunderstormVersion}"
    gitTag "v${currentThunderstormVersion}" "Promoted thunderstorm to: v${thunderstormVersion}"
    gitPushTags
    throwError "Error pushing promotion tag"

    gitNoConflictsAddCommitPush "Thunderstorm" "$(gitGetCurrentBranch)" "published version v${thunderstormVersion}"
  }

  _generate() {
    ((${#ts_generate[@]} == 0)) && return

    logInfo
    bannerInfo "Generate"

    this.apps.forEach generate
  }

  _toLog() {
    logVerbose
    logVerbose "Thunderstorm version: ${thunderstormVersion}"
    logVerbose "App version: ${appVersion}"
    logVerbose

    this.active.forEach toLog
  }
}
