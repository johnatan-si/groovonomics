package carlosgsouza.groovonomics.data
import static groovyx.net.http.ContentType.*
import static groovyx.net.http.Method.*
import groovy.json.JsonBuilder
import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import groovyx.net.http.*
import carlosgsouza.groovonomics.typing_usage.ClassDataFactory
import carlosgsouza.groovonomics.typing_usage.DeclarationCount


class AnalyzeProgrammersBackground {
	JsonSlurper slurper = new JsonSlurper()
	
	File projectsFolder = new File("/Users/carlosgsouza/workspace_gg/groovonomics/articles/aosd_2014/data/type_usage/projects")
	File usersFolder = new File("/Users/carlosgsouza/workspace_gg/groovonomics/articles/aosd_2014/data/users/")
	File outputFolder = new File("/Users/carlosgsouza/workspace_gg/groovonomics/articles/aosd_2014/data/programmers_background/")
	
	def staticallyTypedLanguages = ["Haskell", "Go", "C#", "C++", "Objective-C", "C", "Scala", "Java"]
	
	def goForItBaby() {
		outputFolder.deleteDir()
		outputFolder.mkdirs()
		
		def user_languages = readUserData()
		def project_usage = readProjectData()
		
		def typeSystem_usage = [d:new DeclarationCount(), s:new DeclarationCount(), ds:new DeclarationCount()]
		def usedJava_usage = [java:new DeclarationCount(), nojava:new DeclarationCount()]
		
		project_usage.each { projectId, usage ->
			def (user, name) = parseProjectName(projectId)
			def languages = user_languages[user]
			
			if(languages?.size() > 0) {
				def typeSystem = getTypeSystemOfLanguages(languages)
				typeSystem_usage[typeSystem] += usage
				
				def usedJava = getUsedJava(languages)
				usedJava_usage[usedJava] += usage
			}
		}
		
		new File(outputFolder, "typeSystem.json") << JsonOutput.prettyPrint(new JsonBuilder(typeSystem_usage).toString())
		new File(outputFolder, "java.json") << JsonOutput.prettyPrint(new JsonBuilder(usedJava_usage).toString())
	}
	
	def getUsedJava(languages) {
		languages.contains("Java") ? "java" : "nojava"
	}
	
	def getTypeSystemOfLanguages(languages) {
		def numberOfStaticLanguages = staticallyTypedLanguages.intersect(languages).size()
		
		if(numberOfStaticLanguages == languages.size()) {
			return "s"
		} else if(numberOfStaticLanguages == 0) {
			return "d"
		} else {
			return "ds"
		}
	}
	
	def parseProjectName(projectName) {
		def owner = projectName.substring(0, projectName.lastIndexOf("_"))
		def name = projectName.substring(projectName.lastIndexOf("_")+1)
		
		return [owner, name]
	}
	
	def readProjectData() {
		def result = [:]
		
		projectsFolder.eachFile { projectFile ->
			def projectName = projectFile.name - ".json"
			result[projectName] = agregateProjectUsage(projectFile)
		}
		
		result
	}
	
	def agregateProjectUsage(projectFile) {
		def result = new DeclarationCount()
		
		def all = new ClassDataFactory().fromJsonFile(projectFile)
		
		result += all.publicMethodReturn
		result += all.privateMethodReturn
		result += all.protectedMethodReturn
		result += all.publicField
		result += all.privateField
		result += all.protectedField
		result += all.publicMethodParameter
		result += all.privateMethodParameter
		result += all.protectedMethodParameter
		result += all.publicConstructorParameter
		result += all.privateConstructorParameter
		result += all.protectedConstructorParameter
		result += all.localVariable
		
		return result
	}
	
	def readUserData() {
		def result = [:]
		
		usersFolder.eachFile { userFile ->
			def userName = userFile.name - ".json"
			def languages = userFile.text.split("\n").findAll{it != "Groovy" && it != "" && it != "null"}.unique()
			result[userName] = languages
		}
		result
	}
		
	public static void main(String[] args) {
		def agregator = new AnalyzeProgrammersBackground()
		agregator.goForItBaby()
	}
}