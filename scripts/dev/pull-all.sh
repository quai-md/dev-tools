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

source ${BASH_SOURCE%/*}/../utils/file-tools.sh
source ${BASH_SOURCE%/*}/../_fun/signature.sh
source ${BASH_SOURCE%/*}/tools.sh
source ${BASH_SOURCE%/*}/git-core.sh

pids=()
projectsToIgnore=("dev-tools")
stashName="pull-all-script"
resolution="changed"

function extractParams() {
    for paramValue in "${@}"; do
        case "${paramValue}" in
            "--stash-name="*)
                stashName=`echo "${paramValue}" | sed -E "s/--stash-name=(.*)/\1/"`
            ;;

            "--all")
                resolution="all"
            ;;

            "--project")
                resolution="project"
            ;;

            "--debug")
                debug="true"
            ;;
        esac
    done
}

function printDebugParams() {
    if [ ! "${debug}" ]; then
        return
    fi

    function printParam() {
        if [ ! "${2}" ]; then
            return
        fi

        logDebug "--  ${1}: ${2}"
    }

    logInfo "------- DEBUG: PARAMS -------"
    logDebug "--"
    printParam "stashName" ${stashName}
    printParam "resolution" ${resolution}
    printParam "debug" ${debug}
    logDebug "--"
    logInfo "----------- DEBUG -----------"
    echo
}

function pullRepo() {
    local isClean=`git status | grep "nothing to commit.*"`
    if [ ! "${isClean}" ]; then
        logInfo "${GIT_TAG} Stashing changes with message: ${stashName}"
        result=`git stash save "${stashName}"`
    fi

    gitPullRepo

    if [ ! "${isClean}" ] && [ "${result}" != "No local changes to save" ]; then
        gitStashPop
    fi
}

function processSubmodule() {
    local submodule=${1}

    bannerDebug "Processing: ${submodule}"
    cd ${submodule}
        local submoduleBranch=`gitGetCurrentBranch`
        if [ "${mainRepoBranch}" != "${submoduleBranch}" ]; then
            cd ..
            git submodule udpate ${submodule}
            return
        fi
        pullRepo &
        pid=$!
        pids+=(${pid})
    cd ..
}

mainRepoBranch=`gitGetCurrentBranch`
if [ ! "${mainRepoBranch}" ]; then
    logError "Main repo head detached... "
    exit 1
fi

extractParams "$@"

signature "Pull repo"
printDebugParams

bannerDebug "Processing: Main Repo"
pullRepo

submodules=(`getFolderByResolution ${resolution} "${projectsToIgnore[@]}"`)
for submodule in "${submodules[@]}"; do
    processSubmodule ${submodule}
done

for pid in "${pids[@]}"; do
    wait ${pid}
done