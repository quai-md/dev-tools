@Library('dev-tools')
import com.nu.art.pipeline.workflow.Workflow
import com.nu.art.utils.StringTemplateReplacer
import com.nu.art.pipeline.workflow.BasePipeline

class TemplaterPipeline
	extends BasePipeline<TemplaterPipeline> {

	TemplaterPipeline() {
		super("Test Templater")
	}

	@Override
	protected void init() {
	}

	void pipeline() {
		StringTemplateReplacer.replace("./.jenkins/tests/test-template.txt", "./.jenkins/tests/output.txt")
		_sh 'cat "./.jenkins/tests/output.txt"'
	}
}

node() {
	Workflow.createWorkflow(TemplaterPipeline.class, this)
}

