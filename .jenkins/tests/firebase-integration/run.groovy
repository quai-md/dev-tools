import com.nu.art.pipeline.exceptions.BadImplementationException
@Library('dev-tools@prod')

import com.nu.art.pipeline.modules.SlackModule
import com.nu.art.pipeline.modules.build.BuildModule
import com.nu.art.pipeline.modules.firebase.FirebaseDatabaseModule
import com.nu.art.pipeline.workflow.BasePipeline
import com.nu.art.pipeline.workflow.Workflow
import com.nu.art.pipeline.workflow.variables.Var_Creds

class PipelineTest_FirebaseIntegration
  extends BasePipeline<PipelineTest_FirebaseIntegration> {

  private FirebaseDatabaseModule firebaseDatabaseModule
  private Var_Creds firebaseKey = new Var_Creds("file", "TEMP-test-firebase")

  PipelineTest_FirebaseIntegration() {
    super("Firebase integration", FirebaseDatabaseModule.class)
  }

  @Override
  protected void init() {
    this.setRequiredCredentials(firebaseKey)
    firebaseDatabaseModule = getModule(FirebaseDatabaseModule.class)
    firebaseDatabaseModule.setDefaultProjectId("quai-dev-ops")
    firebaseDatabaseModule.setDefaultDatabaseUrl("https://quai-dev-ops-default-rtdb.firebaseio.com")
  }


  @Override
  void pipeline() {
    firebaseDatabaseModule.install()
    this.testString()
    this.testNumber()
  }

  void testString() {
    String testString = UUID.randomUUID().toString()
    String pathToStringTest = "/testing/test-string"

    addStage("Write String", {
      firebaseDatabaseModule.setValue(pathToStringTest, testString)
    })

    addStage("Read String", {
      String response = firebaseDatabaseModule.getValue(pathToStringTest, "default-test1-results")
      if (response != testString)
        throw new BadImplementationException("expected '${testString}' but got '${response}'")
    })
  }

  void testNumber() {
    int testInt = new Random().nextInt()
    String pathToStringTest = "/testing/test-number"

    addStage("Write Number", {
      firebaseDatabaseModule.setNumber(pathToStringTest, testInt)
    })

    addStage("Read Number", {
      Number response = firebaseDatabaseModule.getNumber(pathToStringTest, null)
      if (response != testInt)
        throw new BadImplementationException("expected '${testInt}' but got '${response}'")
    })
  }
}

node() {
  Workflow.createWorkflow(PipelineTest_FirebaseIntegration.class, this)
}
