@Library('dev-tools@prod')

import com.nu.art.pipeline.workflow.variables.*
import com.nu.art.pipeline.workflow.BasePipeline
import com.nu.art.pipeline.workflow.Workflow

class Pipeline
  extends BasePipeline<Pipeline> {

  Var_Env stringParam = new Var_Env('SOME_STRING', new JobParam('string', 'A string parameter', 'default-value'))
  Var_Env booleanParam = new Var_Env('SOME_BOOLEAN', new JobParam('boolean', 'A boolean parameter', 'true'))
  Var_Env choiceParam = new Var_Env('SOME_CHOICE', new JobParam('choice', 'A choice parameter', 'option1,option2,option3'))
  Var_Env activeParam = new Var_Env('DYNAMIC_PARAM', new JobParam('active-param', 'A dynamic choice', '', '["Option A", "Option B"]'))

  Pipeline() {
    super("Test Params Generation",)
  }

  @Override
  protected void init() {
    this.setJobParams(
      stringParam,
      booleanParam,
      choiceParam,
      activeParam,
    )
  }


  @Override
  void pipeline() {
  }
}

node() {
  Workflow.createWorkflow(Pipeline.class, this)
}
