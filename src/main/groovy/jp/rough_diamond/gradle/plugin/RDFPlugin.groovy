package jp.rough_diamond.gradle.plugin

import org.gradle.api.Project
import org.gradle.api.Plugin

class RDFPlugin implements Plugin<Project> {
	void apply(Project target) {
		target.task('hello', type: GreetingTask)
	}
}
