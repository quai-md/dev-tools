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
    getModule(FirebaseDatabaseModule.class).disable()
    addStage("Read from Database", {
      firebaseDatabaseModule.getValue("/tesing/test1", "test1-results")
    })
  }
}

node() {
  Workflow.createWorkflow(PipelineTest_FirebaseIntegration.class, this)
}
