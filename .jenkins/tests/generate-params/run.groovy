@Library('dev-tools@vdk/dev')

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

    this.logWarning("start")
    def build = workflow.getCurrentBuild()
    this.logWarning("build: ${build.class.getName()}")

    def rawBuild = build.rawBuild
    this.logWarning("rawBuild: ${rawBuild.class.getName()}")

    def parentBuild = rawBuild.parent
    this.logWarning("parentBuild: ${parentBuild.class.getName()}")

    def property = parentBuild.getProperty(hudson.model.ParametersDefinitionProperty.class)
    this.logWarning("property: ${property.class.getName()}")

    def definitions = property?.parameterDefinitions
    this.logWarning("definitions: ${definitions.class.getName()}")

    def currentJobParams = definitions ?: []
    this.logWarning("params length: ${currentJobParams.size()}")
    this.logWarning("params length: ${workflow.script.params}")
    this.logWarning("params length: ${workflow.script.params.getClass().getName()}")
    this.logWarning("params length: ${workflow.script.params[0].class.getName()}")
  }


  @Override
  void pipeline() {
  }
}

node() {
  Workflow.createWorkflow(Pipeline.class, this)
}
