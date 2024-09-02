#!/bin/bash

node.replaceVarsWithValues() {
  local overrideValue=${1}
  local targetFileName=${2:-"package.json"}
  local __packageJson=${3:-"./__package.json"}
  local packageJson="$(file.pathToFile "${__packageJson}")/${targetFileName}"

  file.delete "${packageJson}" -n
  file.copy "${__packageJson}" "" "${packageJson}" -n

  envVars=()
  envVars+=($(file.findMatches "${packageJson}" '"(\$.*?)"'))

  cleanEnvVar() {
    local length=$((${#1} - 3))
    string.substring "${1}" 2 ${length}
  }

  array.map envVars cleanEnvVar
  array.filterDuplicates envVars

  assertExistingVar() {
    local envVar="${1}"
    local version="${!envVar}"
    [[ "${version}" == "" ]] && throwError "no value defined for version key '${envVar}'" 2
  }

  array.forEach envVars assertExistingVar

  replaceWithVersion() {
    local envVar="${1}"
    local version

    if [[ "${overrideValue}" == "" ]]; then
      version="${!envVar}"
    else
      version="${overrideValue}"
    fi

    file.replaceAll ".${envVar}" "${version}" "${packageJson}" %
  }

  array.forEach envVars replaceWithVersion
}
