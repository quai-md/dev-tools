package com.nu.art.pipeline.thunderstorm

import com.nu.art.pipeline.modules.SlackModule
import com.nu.art.pipeline.modules.build.BuildModule
import com.nu.art.pipeline.modules.git.GitModule
import com.nu.art.pipeline.thunderstorm.models.ProjectEnvConfig
import com.nu.art.pipeline.thunderstorm.models.ProjectGitConfig
import com.nu.art.pipeline.workflow.WorkflowModule
import com.nu.art.pipeline.workflow.variables.VarConsts
import com.nu.art.pipeline.workflow.variables.Var_Env

class Pipeline_ThunderstormWebProject<T extends Pipeline_ThunderstormWebProject>
	extends Pipeline_ThunderstormWebApp<T> {

	public Var_Env Env_WorkingEnv = new Var_Env("DEPLOY_TO_ENV")
	public Var_Env Env_Branch = new Var_Env("GIT_CLONE_BRANCH")

	ProjectGitConfig gitConfig
	def envProjects = [:]
	String slackChannel

	Pipeline_ThunderstormWebProject(String name, String slackChannel, Class<? extends WorkflowModule>... modules) {
		super(name, modules)
		this.slackChannel = slackChannel
	}

	@Override
	protected void init() {
		String env = Env_WorkingEnv.get()
		String branch = Env_Branch.get() ?: env
        env = env ?: branch

		getModule(SlackModule.class).setDefaultChannel(this.slackChannel)

		GitModule gitModule = getModule(GitModule.class)
		setRepo(gitModule
			.create(gitConfig.gitRepoUri)
			.setTrackSCM(gitConfig.scm)
			.setBranch(branch)
			.build())


		ProjectEnvConfig envConfig = envProjects.get(env) as ProjectEnvConfig
		String links = ("" +
			"<${envConfig.webAppUrl}|WebApp> | " +
			"<${envConfig.firebaseProjectUrl}|Firebase> | " +
			"<${gitConfig.httpUrl}|Github>").toString()

		getModule(SlackModule.class).setOnSuccess(links)

		setEnv(env)
		super.init()
	}

	void setGitRepoId(String repoId, boolean scm = false) {
		setProjectGitConfig(new ProjectGitConfig(repoId, scm))
	}

	void setProjectGitConfig(ProjectGitConfig gitConfig) {
		this.gitConfig = gitConfig
	}

	void declareEnv(String env, String projectId) {
		declareEnv(env, new ProjectEnvConfig(projectId))
	}

	void declareEnv(String env, ProjectEnvConfig envConfig) {
		envProjects.put(env, envConfig)
	}

	void setDisplayName() {
		def version = getVersion() ? " - v${getVersion()}" : ""
		getModule(BuildModule.class).setDisplayName("#${VarConsts.Var_BuildNumber.get()}: ${getName()} ${this.env} ${version}")
	}

	@Override
	void pipeline() {
		checkout({
			getModule(SlackModule.class).setOnSuccess(getRepo().getChangeLog().toSlackMessage())
		})

		install()
		clean()
		build()
		test()

		deploy()
	}
}
