@Library('dev-tools@prod')

import com.nu.art.pipeline.modules.SlackModule
import com.nu.art.pipeline.modules.build.BuildModule
import com.nu.art.pipeline.workflow.BasePipeline
import com.nu.art.pipeline.workflow.Workflow

class PipelineTest_SlackUploadFile
	extends BasePipeline<PipelineTest_SlackUploadFile> {

	PipelineTest_SlackUploadFile() {
		super("Slack Upload File", SlackModule.class)
	}

	@Override
	protected void init() {
		getModule(SlackModule.class).setToken("quai-slack-token")
		getModule(SlackModule.class).setTeam("quai-md")
		getModule(SlackModule.class).setDefaultChannel("pipeline-temp")
	}


	@Override
	void pipeline() {
		getModule(SlackModule.class).disable()
		addStage("upload file", {

			getModule(SlackModule.class).enableNotifications()
			getModule(SlackModule.class).sendFile(".jenkins/tests/**/dummy-file.txt", "pipeline-temp")

			getModule(SlackModule.class).disable()
		})
	}
}

node() {
	Workflow.createWorkflow(PipelineTest_SlackUploadFile.class, this)
}
