@Library('dev-tools@prod')

import com.nu.art.pipeline.modules.SlackModule
import com.nu.art.pipeline.modules.build.BuildModule
import com.nu.art.pipeline.workflow.BasePipeline
import com.nu.art.pipeline.workflow.Workflow

class Pipeline
  extends BasePipeline<Pipeline> {

  Pipeline() {
    super("Test Params Generation",)
  }

  @Override
  protected void init() {
  }


  @Override
  void pipeline() {
  }
}

node() {
  Workflow.createWorkflow(Pipeline.class, this)
}
