package com.nu.art.pipeline.modules

import com.nu.art.pipeline.modules.build.BuildModule
import com.nu.art.pipeline.workflow.OnPipelineListener
import com.nu.art.pipeline.workflow.Workflow
import com.nu.art.pipeline.workflow.WorkflowModule
import com.nu.art.pipeline.workflow.variables.VarConsts
import com.nu.art.pipeline.workflow.variables.Var_Creds
import com.nu.art.utils.Colors

class SlackModule
	extends WorkflowModule
	implements OnPipelineListener {

	private String token = "slack-token"
	private Var_Creds SlackToken
	private String teamDomain
	private String onSuccess = ""
	private String defaultChannel
	private BuildModule buildModule
	private boolean enabled = true

	@Deprecated
	SlackModule prepare() {
		return this
	}

	void _init() {
		setTokenCredentialsId(this.token)
		buildModule = getModule(BuildModule.class)
	}

	void setToken(String token) {
		this.token = token
	}

	void setTeam(String teamDomain) {
		this.teamDomain = teamDomain
	}

	void disable() {
		this.enabled = false
	}

	void enableNotifications() {
		this.enabled = true
	}

	void setOnSuccess(String onSuccess) {
		this.onSuccess += "\n$onSuccess"
	}

	void setTokenCredentialsId(String tokenCredentialId) {
		SlackToken = new Var_Creds("string", tokenCredentialId)
	}

	void setDefaultChannel(String defaultChannel) {
		this.defaultChannel = defaultChannel
	}

	void notify(GString message, String color, String channelName = defaultChannel, Boolean showTitle = true) {
		notify(message.toString(), color, channelName, showTitle)
	}

	void notify(String message, String color = null, String channelName = defaultChannel, Boolean showTitle = true) {
		if (!enabled)
			return

		String email = VarConsts.Var_User.get()
		String preMessage = ""
		if (showTitle) {
			preMessage += "<${VarConsts.Var_BuildUrl.get()}|*${buildModule.getDisplayName()}*>"
			preMessage += workflow.currentStage != Workflow.Stage_Started ? " after: ${buildModule.getDurationAsString()}" : ""
			preMessage += email != null ? "\nTriggered By: *${email}*" : ""
			preMessage += buildModule.getDescription() ? "\n${buildModule.getDescription()}" : ""
			preMessage += "\n"
		}
		String finalMessage = "${preMessage}${message}"
		finalMessage = finalMessage
			.replaceAll(/<b>/, "*")
			.replaceAll(/<\/b>/, "*")
			.replaceAll(/<br>/, "\n")
			.replaceAll(/<\/br>/, "\n")

		workflow.script.slackSend(botUser: true, color: color, teamDomain: teamDomain, channel: channelName, message: finalMessage, tokenCredentialId: SlackToken.id)
	}

	void sendFile(String filePath, String channelName = defaultChannel) {
		if (!enabled)
			return

		this.logInfo("filePath: ${filePath}, channelName: ${channelName}, credentialId: ${SlackToken.id}")
		// Assuming filePath is valid and accessible in the current workspace
		try {
			workflow.script.slackUploadFile(
				botUser: true,
				filePath: filePath,
				channel: channelName,
				credentialId: SlackToken.id, // Ensure this is the credential ID for a bot user token
				tokenCredentialId: SlackToken.id, // Ensure this is the credential ID for a bot user token
			)
		} catch (e) {
			// Handle errors appropriately
			this.logError(e)
		}
	}

	@Override
	void onPipelineStarted() {
		notify("*Started*", Colors.LightGray)
	}

	void onPipelineAborted() {
		notify("*Aborted* in stage: ${workflow.currentStage}", Colors.DarkGray)
	}

	@Override
	void onPipelineFailed(Throwable e) {
		notify("*Error* in stage: ${workflow.currentStage}", Colors.Red)
	}

	@Override
	void onPipelineSuccess() {
		notify("*Success*${onSuccess ? "\n${onSuccess}" : ""}", Colors.Green)
	}
}
