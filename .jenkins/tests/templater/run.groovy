import com.nu.art.pipeline.modules.git.GitModule
@Library('dev-tools')
import com.nu.art.pipeline.workflow.Workflow
import com.nu.art.utils.StringTemplateReplacer
import com.nu.art.pipeline.workflow.BasePipeline
import com.nu.art.pipeline.modules.git.GitModule
import com.nu.art.pipeline.modules.build.BuildModule
import com.nu.art.pipeline.modules.git.GitRepo

class PipelineTest_Templater
	extends BasePipeline<PipelineTest_Templater> {

	PipelineTest_Templater() {
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

		this.workflow.setEnvironmentVariable("TEST1", "lovely")
		this.workflow.setEnvironmentVariable("PARAM", "friendly")

		String fromFile = getModule(BuildModule.class).pathToFile("./.jenkins/tests/templater/test-template.txt")
		String toFile = getModule(BuildModule.class).pathToFile("./.jenkins/tests/templater/output.txt")
		StringTemplateReplacer.replace(fromFile, toFile)
		_sh 'cat "./.jenkins/tests/templater/output.txt"'
	}
}

node() {
	Workflow.createWorkflow(PipelineTest_Templater.class, this)
}

