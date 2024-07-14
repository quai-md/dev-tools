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
source ${BASH_SOURCE%/*}/../android/_source.sh

executeCommand() {
    local command=${1}
    local message=${2}
    if [[ ! "${message}" ]]; then message="Running: ${1}"; fi
    logInfo "${message}"
    eval "${command}"
    throwError "${message}"
}

signature "Jenkins Setup"
executeCommand "sudo apt-get update"

# Installing packages
executeCommand "sudo apt-get install -y unzip" "Installing unzip"
executeCommand "sudo apt-get install -y zip" "Installing zip"

# Set 16 gb swap
executeCommand "sudo fallocate -l 16G /swapfile" "Setup 16gb swapfile"
executeCommand "sudo chmod 600 /swapfile" "chmod 600 for swapfile"
executeCommand "sudo mkswap /swapfile" "Make swap to swapfile"
executeCommand "sudo swapon /swapfile" "Enable swap"

executeCommand "sudo wget -O /usr/share/keyrings/jenkins-keyring.asc https://pkg.jenkins.io/debian-stable/jenkins.io-2023.key" "Resolving Jenkins - 1"
executeCommand "echo \"deb [signed-by=/usr/share/keyrings/jenkins-keyring.asc]\" https://pkg.jenkins.io/debian-stable binary/ | sudo tee /etc/apt/sources.list.d/jenkins.list > /dev/null" "Resolving Jenkins - 2"

executeCommand "sudo apt-get update"
executeCommand "sudo apt-get install -y jenkins" "Install Jenkins"
executeCommand "sudo systemctl start jenkins" "Start Jenkins"

executeCommand "sudo cat /var/lib/jenkins/secrets/initialAdminPassword" "Displaying Jenkins Admin Password"
