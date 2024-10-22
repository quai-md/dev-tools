@Library('dev-tools@vdk/dev')

import com.nu.art.pipeline.workflow.variables.*
import com.nu.art.pipeline.workflow.BasePipeline
import com.nu.art.pipeline.workflow.Workflow
import java.lang.reflect.Modifier

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
    this.logWarning("params length: ${currentJobParams.get(0).getClass().getName()}")
//    this.logWarning("params length: ${currentJobParams.collect jobParam -> { "${jobParam.getName()}(${jobParam.getType()})\ndesc: ${jobParam.getDescription()}\n " }}")
    printClassSignature(currentJobParams.get(0))
    this.logWarning("params length: ${workflow.script.params}")
    this.logWarning("params length: ${workflow.script.params.getClass().getName()}")
    this.logWarning("params length: ${workflow.script.params[0].class.getName()}")
  }


  @Override
  void pipeline() {
  }

  void printClassSignature(Object instance) {
    if (instance == null) {
      println("The instance is null, cannot determine class.")
      return
    }

    // Get the class of the instance
    Class<?> clazz = instance.getClass()
    this.logError("Class: ${clazz.name}")

    // Print class modifiers (public, abstract, etc.)
    this.logError("${Modifier.toString(clazz.modifiers)} class ${clazz.simpleName} {")

    // Print fields (including their types)
    this.logError("\n  // Fields")
    clazz.declaredFields.each { field ->
      def modifiers = Modifier.toString(field.modifiers)
      this.logError("  ${modifiers} ${field.type.simpleName} ${field.name};")
    }

    // Print constructors (including parameter types)
    this.logError("\n  // Constructors")
    clazz.declaredConstructors.each { constructor ->
      def modifiers = Modifier.toString(constructor.modifiers)
      def paramTypes = constructor.parameterTypes.collect { it.simpleName }.join(', ')
      this.logError("  ${modifiers} ${clazz.simpleName}(${paramTypes});")
    }

    // Print methods (including return types and parameters)
    this.logError("\n  // Methods")
    clazz.declaredMethods.each { method ->
      def modifiers = Modifier.toString(method.modifiers)
      def returnType = method.returnType.simpleName
      def paramTypes = method.parameterTypes.collect { it.simpleName }.join(', ')
      this.logError("  ${modifiers} ${returnType} ${method.name}(${paramTypes});")
    }

    // Close the class signature
    this.logError("}")
  }

}

node() {
  Workflow.createWorkflow(Pipeline.class, this)
}
