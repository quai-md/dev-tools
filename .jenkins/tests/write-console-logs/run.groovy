@Library('dev-tools@prod')

import com.nu.art.pipeline.workflow.BasePipeline
import com.nu.art.pipeline.workflow.Workflow

class PipelineTest_WriteLogs
	extends BasePipeline<PipelineTest_WriteLogs> {

	PipelineTest_WriteLogs() {
		super("Write Logs")
	}

	@Override
	protected void init() {}

	@Override
	void pipeline() {
        _sh("""
                    ±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±
        			echo "Echoed Hello World"
        			±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±
        	""")
        this.logDebug("±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±\nLogged Hello World\n±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±")

	}
}

node() {
	Workflow.createWorkflow(PipelineTest_WriteLogs.class, this)
}
