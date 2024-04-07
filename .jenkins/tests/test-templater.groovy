@Library('dev-tools@pipeline')

import com.nu.art.utils.StringTemplateReplacer

pipeline {
//	agent {
//		kubernetes {
//			defaultContainer 'base'
//			yamlFile './pod-templates/base.yaml'
//		}
//	}
//

	stages {
		stage('test templater') {
			steps {
				env.PARAM = "lovely"
//				env.PLACEHOLDER = "zevel"
				env.TEST1 = "friendly"
				StringTemplateReplacer.replace("./.jenkins/tests/test-template.txt", "./.jenkins/tests/output.txt")
				sh 'cat "./.jenkins/tests/output.txt"'
			}
		}
	}
}

