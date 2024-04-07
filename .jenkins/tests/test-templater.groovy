@Library('dev-tools')

import com.nu.art.utils.StringTemplateReplacer

node() {
	stages {
		stage('test templater') {
//			env.PARAM = "lovely"
////				env.PLACEHOLDER = "zevel"
//			env.TEST1 = "friendly"
			StringTemplateReplacer.replace("./.jenkins/tests/test-template.txt", "./.jenkins/tests/output.txt")
			sh 'cat "./.jenkins/tests/output.txt"'
		}
	}
}

