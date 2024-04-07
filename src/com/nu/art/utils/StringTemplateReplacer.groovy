package com.nu.art.utils

class StringTemplateReplacer {

	static void replace(String fromFile, String toFile) {
		// Read content from the source file
		String content = new File(fromFile).text

		// Regex to find {{VAR_NAME}} patterns
		String pattern = /\{\{([a-zA-Z_][a-zA-Z_0-9]+)(?:=(.*?))?\}\}/

		// Replace each found pattern with the value from Jenkins environment variable
		String modifiedContent = content.replaceAll(pattern) { fullMatch, varName, defaultValue ->
			// Attempt to fetch the environment variable value
			String varValue = env[varName]

			// Use the default value if the environment variable is not found or is empty
			if (!varValue) {
				if (defaultValue != null) {
					// Default value specified and variable not found, use default
					varValue = defaultValue
				} else {
					// Variable not found and no default specified, throw exception
					throw new Exception("Environment variable '${varName}' not found and no default value specified.")
				}
			}

			return varValue
		}


		// Write the modified content to the destination file
		new File(toFile).text = modifiedContent
	}
}
