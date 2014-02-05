package jp.rough_diamond.gradle.plugin

import java.awt.color.ProfileDataException;

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction;

import aQute.bnd.build.Project;

class SetupProfileTask extends DefaultTask {
	String DEFAULT_ENV = java.lang.System.getProperty("user.name").replaceAll(/-/, "_")
	String propertyFile = "profile.properties"
	String resourceDir = "src/main/resources"
	String templatePath = "profiles/resources"
	
	SetupProfileTask() {
		setDescription("Apply Profile for your environment")	
//		println project.class.name
//		println project.tasks
		def tasks = project.getTasksByName("processResources", false)
//		println tasks.class.name
//		println tasks
		tasks.each{ it.dependsOn(this) }
	}
	
	@TaskAction
	def setupProfile() {
		String environment = DEFAULT_ENV
		if (project.hasProperty('env')) {
			environment = project.property('env')
		} else {
			environment = System.env['GRADLE_ENV'] ?: DEFAULT_ENV 
		}
		println environment
		File defaultProfile = new File("profiles/default.profile")
		File profile = new File("profiles/" + environment + ".profile")
		def config = new ConfigSlurper().parse(new Properties())
		if(defaultProfile.exists()) {
			config = config.merge(new ConfigSlurper().parse(defaultProfile.toURL()))
		}
		if(profile.exists()) {
			config = config.merge(new ConfigSlurper().parse(profile.toURL()));
		}
		if(config.isEmpty()) {
			println "環境依存情報が存在しないので処理を終了します。"
			return
		}
		def prop = config.toProperties()
		File propFile = new File(new File(resourceDir), propertyFile)
		println propFile.absolutePath;
		propFile.parentFile.mkdirs()
		prop.store(new FileOutputStream(propFile), environment + " environment profile.");
	
		if(!new File(templatePath).exists()) {
			println "テンプレートが無いので終了します"
			return
		}
		ant.filter(filtersFile:propFile.absolutePath)
		ant.copy(todir:resourceDir, filtering:"true", overwrite:"true", encoding:"UTF-8"){
		  fileset(dir:templatePath)
		}
	}
}
