import com.nu.art.pipeline.modules.git.GitModule
@Library('dev-tools')
import com.nu.art.pipeline.workflow.Workflow
import com.nu.art.utils.StringTemplateReplacer
import com.nu.art.pipeline.workflow.BasePipeline
import com.nu.art.pipeline.modules.git.GitModule
import com.nu.art.pipeline.modules.build.BuildModule
import com.nu.art.pipeline.modules.git.GitRepo

class TemplaterPipeline
	extends BasePipeline<TemplaterPipeline> {

	TemplaterPipeline() {
		super("Test Templater", BuildModule.class, GitModule.class)
	}

	@Override
	protected void init() {
	}

	void pipeline() {

		GitModule gitModule = getModule(GitModule.class)
		GitRepo repo = gitModule
			.create("https://github.com/nu-art/dev-tools.git")
			.setBranch("prod")
			.setOutputFolder(".")
			.build()

		repo.cloneRepo()

		_sh '''
					pwd
					ls -la ./.jenkins/tests
				'''

		String fromFile = getModule(BuildModule.class).pathToFile("./.jenkins/tests/test-template.txt")
		String toFile = getModule(BuildModule.class).pathToFile("./.jenkins/tests/output.txt")
		StringTemplateReplacer.replace(fromFile, toFile)
		_sh 'cat "./.jenkins/tests/output.txt"'
	}
}

node() {
	Workflow.createWorkflow(TemplaterPipeline.class, this)
}

