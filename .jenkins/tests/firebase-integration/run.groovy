import com.nu.art.pipeline.exceptions.BadImplementationException
@Library('dev-tools@dev')

import com.nu.art.pipeline.modules.firebase.FirebaseDatabaseModule
import com.nu.art.pipeline.workflow.BasePipeline
import com.nu.art.pipeline.workflow.Workflow
import com.nu.art.pipeline.workflow.variables.Var_Creds
import com.nu.art.pipeline.workflow.variables.Var_Env

class PipelineTest_FirebaseIntegration
  extends BasePipeline<PipelineTest_FirebaseIntegration> {

  private FirebaseDatabaseModule firebaseDatabaseModule
  public Var_Creds Env_FirebaseServiceAccount = new Var_Creds("file", "TEMP-test-firebase", new Var_Env("GOOGLE_APPLICATION_CREDENTIALS"))

  PipelineTest_FirebaseIntegration() {
    super("Firebase integration", FirebaseDatabaseModule.class)
  }

  @Override
  protected void init() {
    this.setRequiredCredentials(Env_FirebaseServiceAccount)
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
    bash("firebase list")

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

podTemplate(yaml: '''
    apiVersion: v1
    kind: Pod
    metadata:
      annotations:
        cluster-autoscaler.kubernetes.io/safe-to-evict: "false"
    spec:
      containers:
        - name: test-container
          image: us-docker.pkg.dev/quai-md-jenkins-new-test-v1/jenkins-old-server/jenkins-slave:22-05-24-15h-04m
          tty: true
          resources:
            limits:
              cpu: "1"
              memory: "2Gi"
            requests:
              cpu: "1"
              memory: "2Gi"
          command:
            - sleep
          args:
            - "9999999"
    
      restartPolicy: Never
  ''', activeDeadlineSeconds: 7200, instanceCap: 10) {
  node(POD_LABEL) {
    container('test-container') {
      Workflow.createWorkflow(PipelineTest_FirebaseIntegration.class, this)
    }
  }
}
