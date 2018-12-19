#
#  This file is a part of nu-art projects development tools,
#  it has a set of bash and gradle scripts, and the default
#  settings for Android Studio and IntelliJ.
#
#     Copyright (C) 2017  Adam van der Kruk aka TacB0sS
#
#  Licensed under the Apache License, Version 2.0 (the "License");
#  you may not use this file except in compliance with the License.
#          You may obtain a copy of the License at
#
#  http://www.apache.org/licenses/LICENSE-2.0
#
#  Unless required by applicable law or agreed to in writing, software
#  distributed under the License is distributed on an "AS IS" BASIS,
#  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
#  See the License for the specific language governing permissions and
#  limitations under the License.

#!/bin/bash

source ${BASH_SOURCE%/*}/_core.sh

paramColor=${BRed}
projectsToIgnore=("dev-tools")
params=(githubUsername fromBranch toBranch)

function extractParams() {
    for paramValue in "${@}"; do
        case "${paramValue}" in
            "--github-username="*)
                githubUsername=`echo "${paramValue}" | sed -E "s/--github-username=(.*)/\1/"`
            ;;

            "--from="*)
                fromBranch=`echo "${paramValue}" | sed -E "s/--from=(.*)/\1/"`
            ;;

            "--to="*)
                toBranch=`echo "${paramValue}" | sed -E "s/--to=(.*)/\1/"`
            ;;

            "--debug")
                debug="true"
            ;;
        esac
    done
}

function printUsage() {
    logVerbose
    logVerbose "   USAGE:"
    logVerbose "     ${BBlack}bash${NoColor} ${BCyan}${0}${NoColor} --from=${fromBranch} --to=${toBranch}"
    logVerbose
    exit 0
}

function verifyRequirement() {
    missingData=false
    if [ "${fromBranch}" == "" ]; then
        fromBranch="${paramColor}Branch-to-be-merged-from${NoColor}"
        missingData=true
    fi

    if [ "${toBranch}" == "" ]; then
        toBranch="${paramColor}Branch-to-merge-onto${NoColor}"
        missingData=true
    fi

    if [ "${missingData}" == "true" ]; then
        printUsage
    fi

}
extractParams "$@"
verifyRequirement

currentBranch=`gitGetCurrentBranch`
if [ "${currentBranch}" != "${fromBranch}" ]; then
    logError "Main Repo MUST be on branch: ${fromBranch}"
    exit 1
fi

summary=""

function processFolder() {
    local submoduleName=${1}
    local currentBranch=`gitGetCurrentBranch`
    if [ "${currentBranch}" != "${fromBranch}" ]; then
        logVerbose "repo '${submoduleName}'is not aligned with branch: ${fromBranch}!!"
        return
    fi

    if [[ ! `git status` =~ "Your branch is up to date with 'origin/${fromBranch}'" ]]; then
        logError "repo '${submoduleName}'is not synced with origin!!"
        git status
        exit 1
    fi

    local project=`getGitRepoName`
    checkExecutionError "Unable to extract remote project name"

    url="https://github.com/${project}/compare/${toBranch}...${fromBranch}?expand=1"
    echo "URL: ${url}"
    open ${url}
    sleep 2s
    summary="${summary}\nhttps://github.com/${project}/pulls/${githubUsername}"
}

signature "Pull-Request"
printDebugParams ${debug} "${params[@]}"

bannerDebug "Processing: Main Repo"
processFolder "Main Repo"
iterateOverFolders "gitListSubmodules" processFolder

logVerbose "${summary}"
