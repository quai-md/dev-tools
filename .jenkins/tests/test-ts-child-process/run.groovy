@Library('dev-tools@prod')

import com.nu.art.pipeline.modules.git.GitModule
import com.nu.art.pipeline.modules.git.GitRepo
import com.nu.art.pipeline.workflow.BasePipeline
import com.nu.art.pipeline.workflow.Workflow

class PipelineTest_ChildProcessTS
	extends BasePipeline<PipelineTest_ChildProcessTS> {

	PipelineTest_ChildProcessTS() {
		super("Test TS Child Process", GitModule.class)
	}

	@Override
	protected void init() {
	}

	@Override
	void pipeline() {
		GitModule gitModule = getModule(GitModule.class)
		GitRepo repo = gitModule
			.create("https://github.com/nu-art/dev-tools.git")
			.setBranch("prod")
			.setOutputFolder(".")
			.build()

		repo.cloneRepo()

		_sh("""echo 18.15.0>.nvmrc""") // set nvm version
		_sh("""curl -o- "https://raw.githubusercontent.com/nvm-sh/nvm/v0.39.7/install.sh" | bash""") // install nvm


		_sh("""
cat <<EOF >package.json
	{
		"name": "temp",
		"version": "0.0.1",
		"devDependencies": {
			"@types/node": "^18.15.0",
			"@nu-art/build-and-install": "~0.204.38",
			"@nu-art/commando": "~0.204.38",
			"@nu-art/ts-common": "~0.204.38"
		}
	}
EOF
""")
		_sh("""
				#!/bin/bash
				export NVM_DIR="\$HOME/.nvm"
				[ -s "\$NVM_DIR/nvm.sh" ] && \\. "\$NVM_DIR/nvm.sh"  # This loads nvm
				nvm install
				nvm use
				npm i -g ts-node@latest
				ts-node ./.jenkins/tests/test-ts-child-process/test.ts
""")
	}
}

node() {
	Workflow.createWorkflow(PipelineTest_ChildProcessTS.class, this)
}
