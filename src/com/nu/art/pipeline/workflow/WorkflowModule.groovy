package com.nu.art.pipeline.workflow

import com.cloudbees.groovy.cps.NonCPS
@Grab('com.nu-art-software:module-manager:1.2.34')

import com.nu.art.modular.core.Module

abstract class WorkflowModule
	extends Module {

	@NonCPS
	@Override
	protected void init() {}

	void _init() {}

	@SuppressWarnings('GrMethodMayBeStatic')
	Workflow getWorkflow() {
		Workflow.workflow
	}

	def <R> R cd(String path, Closure<R> closure) {
		return workflow.cd(path, closure)
	}

	String sh(String command, readOutput = false) {
		return workflow.sh(command, readOutput)
	}

	String bash(String command, readOutput = false) {
		return workflow.bash(command, readOutput)
	}

	String sh(GString command, readOutput = false) {
		return workflow.sh(command.toString(), readOutput)
	}
}
