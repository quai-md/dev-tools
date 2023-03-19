#!/bin/bash
FOLDER_Config="${Path_RootRunningDir}/.firebase_config"

BackendPackageV2() {
  extends class NodePackageV2

  _deploy() {
    [[ ! "$(array_contains "${folderName}" "${ts_activeLibs[@]}")" ]] && return
    [[ ! "$(array_contains "${folderName}" "${ts_deploy[@]}")" ]] && return
    [[ ! "$(array_contains "${folderName}" "${deployableApps[@]}")" ]] && return

    logInfo "Deploying: ${folderName}"
    ${CONST_Firebase} --debug deploy --only functions
    throwWarning "Error while deploying functions"
    logInfo "Deployed: ${folderName}"
  }

  _setEnvironment() {
    #    TODO: iterate on all source folders
    logDebug "Setting ${folderName} env: ${ts_envType}"
    copyConfigFile "./.config/config-ENV_TYPE.ts" "./src/main/config.ts" "${ts_envType}" "${fallbackEnv}"
  }

  _compile() {
    logInfo "Compiling: ${folderName}"

    [[ -e "${Path_RootRunningDir}/version-app.json" ]] && copyFileToFolder "${Path_RootRunningDir}/version-app.json" "./src/main" && copyFileToFolder "${Path_RootRunningDir}/version-app.json" "./dist"
    copyFileToFolder "./package.json" "${outputDir}"

    for lib in ${@}; do
      [[ "${lib}" == "${_this}" ]] && break
      local libPath="$("${lib}.path")"
      local libFolderName="$("${lib}.folderName")"
      local libPackageName="$("${lib}.packageName")"

      [[ ! "$(cat package.json | grep "${libPackageName}")" ]] && continue

      local backendDependencyPath="./.dependencies/${libFolderName}"
      deleteDir "${backendDependencyPath}"
      createDir "${backendDependencyPath}"
      _logWarning "Copying "${libPath}/${libFolderName}/${outputDir}" => ${backendDependencyPath}"
      cp -rf "${libPath}/${libFolderName}/${outputDir}"/* "${backendDependencyPath}/"
      file_replace "\"${libPackageName}\": \".*\"" "\"${libPackageName}\": \"file:.dependencies/${libFolderName}\"" "${outputDir}/package.json" "" "%"
    done

    npm run build
    throwWarning "Error compiling: ${folderName}"
  }

  _generate() {
    [[ ! "$(array_contains "${folderName}" "${ts_generate[@]}")" ]] && return

    logInfo "Generating: ${folderName}"
  }

  _launch() {
    [[ ! "$(array_contains "${folderName}" "${ts_launch[@]}")" ]] && return

    logInfo "Prepare indexes and rules: ${folderName}"
    if [[ ! -e "${FOLDER_Config}/database.rules.json" ]]; then
      copyFileToFolder "${Path_RootRunningDir}/dev-tools/scripts/dev/typescript-oo/templates/firebase_config/database.rules.json" "${FOLDER_Config}"
    fi

    if [[ ! -e "${FOLDER_Config}/firestore.indexes.json" ]]; then
      copyFileToFolder "${Path_RootRunningDir}/dev-tools/scripts/dev/typescript-oo/templates/firebase_config/firestore.indexes.json" "${FOLDER_Config}"
    fi

    if [[ ! -e "${FOLDER_Config}/firestore.rules" ]]; then
      copyFileToFolder "${Path_RootRunningDir}/dev-tools/scripts/dev/typescript-oo/templates/firebase_config/firestore.rules" "${FOLDER_Config}"
    fi

    if [[ ! -e "${FOLDER_Config}/storage.rules" ]]; then
      copyFileToFolder "${Path_RootRunningDir}/dev-tools/scripts/dev/typescript-oo/templates/firebase_config/storage.rules" "${FOLDER_Config}"
    fi

    logInfo "Launching: ${folderName}"
    if [[ "${ts_debugBackend}" == "--debug" ]]; then
      npm run debug
    else
      npm run launch
    fi
  }

  _clean() {
    this.NodePackageV2.clean
    deleteDir ".dependencies"
  }
}
