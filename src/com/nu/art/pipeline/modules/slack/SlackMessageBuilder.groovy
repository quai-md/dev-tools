package com.nu.art.pipeline.modules.slack

import com.nu.art.pipeline.modules.SlackModule

class SlackMessageBuilder {

	private SlackMessage message = new SlackMessage()
	private SlackModule slackModule

	SlackMessageBuilder(SlackModule slackModule) {
		this.slackModule = slackModule
	}


	SlackMessageBuilder setTeamDomain(String teamDomain) {
		message.teamDomain = teamDomain
		return this
	}

	SlackMessageBuilder setColor(String color) {
		message.color = color
		return this
	}

	SlackMessageBuilder setChannel(String channel) {
		message.channel = channel
		return this
	}

	SlackMessageBuilder appendLink(String label) {
		message.message += slackModule.getLinkToJob(label)
		return this
	}

	SlackMessageBuilder appendTriggerCause() {
		message.message += slackModule.getTriggerCause()
		return this
	}

	SlackMessageBuilder appendTimeFrom(String label) {
		message.message += slackModule.getTimeFromStart(label)
		return this
	}

	SlackMessageBuilder append(String label) {
		message.message += label
		return this
	}

	SlackMessageBuilder newLine() {
		message.message += "\n"
		return this
	}

	void send() {
		slackModule.sendMessage(message)
	}
}
