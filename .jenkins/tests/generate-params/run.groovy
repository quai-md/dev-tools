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
  }


  @Override
  void pipeline() {
    Map<String, JobParam> paramsMap = [
      stringParam,
      booleanParam,
      choiceParam,
      activeParam,
    ].collectEntries { varEnv ->
      [(varEnv.varName): varEnv.param]
    }

    Map<String, Object> jobParamsMap = getDefinedParamsInJob()
    compareJobParams(paramsMap, jobParamsMap)


    def collect = currentJobParams.collect { p ->
      """
         ${p.getName()}(${p.getType()})
         desc: ${p.getDescription()}
      """
    }
    this.logWarning("params length: ${collect.join("\n")}}")
  }

  private void getDefinedParamsInJob() {
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
    currentJobParams.collectEntries { param ->
      [(param.getName()): param]
    }
  }

  void compareJobParams(Map<String, JobParam> paramsMap, Map<String, Object> jobParamsMap) {
    def missingInJob = paramsMap.keySet().findAll { !jobParamsMap.containsKey(it) }
    def extraInJob = jobParamsMap.keySet().findAll { !paramsMap.containsKey(it) }

    def modifiedParams = paramsMap.collectEntries { paramName, newParam ->
      def currentParam = jobParamsMap[paramName]
      if (!currentParam) {
        return null // Already handled in missingInJob
      }

      // Check for differences in parameter types or values
      def isModified = false

      // Type comparison
      if (getParamType(currentParam) != newParam.type) {
        isModified = true
      } else {
        // For string and boolean, compare the defaultValue and description
        switch (newParam.type) {
          case 'string':
          case 'boolean':
          case 'password':
          case 'text':
            if (currentParam.defaultValue != newParam.defaultValue ||
              currentParam.description != newParam.description) {
              isModified = true
            }
            break

          case 'choice':
            def currentChoices = currentParam.choices.join(',')
            def newChoices = newParam.defaultValue.split(',')
            if (currentChoices != newChoices || currentParam.description != newParam.description) {
              isModified = true
            }
            break

          case 'active-param':
            if (currentParam.script != newParam.script || currentParam.description != newParam.description) {
              isModified = true
            }
            break

            // Add further comparison logic for other types if needed
        }
      }

      return isModified ? [(paramName): [current: currentParam, new: newParam]] : null
    }.findAll()

    // Log the differences for the user's visibility
    logParamDifferences(missingInJob, extraInJob, modifiedParams)

    if (modifiedParams.size() === 0)
      return

    this.setJobParams(
      stringParam,
      booleanParam,
      choiceParam,
      activeParam,
    )
  }

  String getParamType(def param) {
    switch (param.class.simpleName) {
      case 'StringParameterDefinition':
        return 'string'
      case 'BooleanParameterDefinition':
        return 'boolean'
      case 'ChoiceParameterDefinition':
        return 'choice'
      case 'PasswordParameterDefinition':
        return 'password'
      case 'TextParameterDefinition':
        return 'text'
        // Add more types as needed
      default:
        return 'unknown'
    }
  }

  void logParamDifferences(def missingInJob, def extraInJob, def modifiedParams) {
    if (missingInJob) {
      this.logWarning("Parameters missing in job: ${missingInJob.join(', ')}")
    }
    if (extraInJob) {
      this.logWarning("Parameters extra in job (not in definition): ${extraInJob.join(', ')}")
    }
    if (modifiedParams) {
      this.logWarning("Modified parameters:")
      modifiedParams.each { paramName, diff ->
        this.logWarning("Param '${paramName}' differs: current=${diff.current}, new=${diff.new}")
      }
    }
  }

//  void printClassSignature(Object instance) {
//    if (instance == null) {
//      println("The instance is null, cannot determine class.")
//      return
//    }
//
//    Class<?> clazz = instance.getClass()
//    printClass(clazz)
//  }
//
//  private void printClass(Class<?> clazz) {
//    def toPrint = ""
//    // Get the class of the instance
//    toPrint += "Class: ${clazz.name}"
//
//    // Print class modifiers (public, abstract, etc.)
//    toPrint += "\n${Modifier.toString(clazz.modifiers)} class ${clazz.simpleName} {"
//
//    // Print fields (including their types)
//    toPrint += "\n\n  // Fields"
//    clazz.getFields().each { field ->
//      def modifiers = Modifier.toString(field.modifiers)
//      toPrint += "\n  ${modifiers} ${field.type.simpleName} ${field.name};"
//    }
//
//    // Print constructors (including parameter types)
//    toPrint += "\n\n  // Constructors"
//    clazz.declaredConstructors.each { constructor ->
//      def modifiers = Modifier.toString(constructor.modifiers)
//      def paramTypes = constructor.parameterTypes.collect { it.simpleName }.join(', ')
//      toPrint += "\n  ${modifiers} ${clazz.simpleName}(${paramTypes});"
//    }
//
//    // Print methods (including return types and parameters)
//    toPrint += "\n\n  // Methods"
//    clazz.declaredMethods.each { method ->
//      def modifiers = Modifier.toString(method.modifiers)
//      def returnType = method.returnType.simpleName
//      def paramTypes = method.parameterTypes.collect { it.simpleName }.join(', ')
//      toPrint += "\n  ${modifiers} ${returnType} ${method.name}(${paramTypes});"
//    }
//
//    // Close the class signature
//    toPrint += "\n}"
//    this.logError(toPrint)
//
//    Class<?> superClazz = clazz.getSuperclass()
//    if (superClazz != Object.class)
//      printClassSignature(superClazz)
//  }

}

node() {
  Workflow.createWorkflow(Pipeline.class, this)
}
