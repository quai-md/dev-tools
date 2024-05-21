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
				echo 'export NVM_DIR="\$HOME/.nvm"' > ./install-nvm.sh
				echo '[ -s "\$NVM_DIR/nvm.sh" ] && \\. "\$NVM_DIR/nvm.sh"  # This loads nvm' >> ./install-nvm.sh

				echo "cat file"
				cat ./install-nvm.sh
				echo "end of cat file"

				bash ./install-nvm.sh
""")
		_sh("""nvm use""") // nvm rc
		_sh("""npm i -g ts-node@latest""") // install ts-node global
		_sh("""ts-node ./test.ts""") // install ts-node global
	}
}

node() {
	Workflow.createWorkflow(PipelineTest_ChildProcessTS.class, this)
}
