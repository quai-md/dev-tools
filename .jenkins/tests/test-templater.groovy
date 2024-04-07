@Library('dev-tools')

import com.nu.art.utils.StringTemplateReplacer

pipeline {
	agent any

	stages {
		stage('test templater') {
//			env.PARAM = "lovely"
////				env.PLACEHOLDER = "zevel"
//			env.TEST1 = "friendly"
			script {
				StringTemplateReplacer.replace("./.jenkins/tests/test-template.txt", "./.jenkins/tests/output.txt")
				sh 'cat "./.jenkins/tests/output.txt"'
			}
		}
	}
}

