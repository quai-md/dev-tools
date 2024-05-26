package com.nu.art.pipeline.thunderstorm

import com.nu.art.pipeline.workflow.WorkflowModule

class Pipeline_ThunderstormWebApp<T extends Pipeline_ThunderstormWebApp>
	extends Pipeline_ThunderstormCore<T> {

	protected String env

	Pipeline_ThunderstormWebApp(GString name, Class<? extends WorkflowModule>... modules = []) {
		this(name.toString(), modules)
	}

	Pipeline_ThunderstormWebApp(String name, Class<? extends WorkflowModule>... modules = []) {
		super(name, modules)
	}

	Pipeline_ThunderstormWebApp(Class<? extends WorkflowModule>... modules = []) {
		super(modules)
	}

	protected void setEnv(String env) {
		this.env = env
	}

	protected T deploy() {
		addStage("deploy", { this._deploy() })
		return this as T
	}

	protected void _install() {
		_sh("bash build-and-install.sh --set-env=${this.env} --install --no-build --link --debug")
	}

	protected void _deploy() {
		_sh("bash build-and-install.sh --print-env")
		_sh("bash build-and-install.sh --deploy --quick-deploy --no-git  --debug")
	}

	@Override
	void pipeline() {}
}
