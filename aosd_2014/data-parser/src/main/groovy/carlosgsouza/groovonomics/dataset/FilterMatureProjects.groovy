import static groovyx.net.http.ContentType.*
import static groovyx.net.http.Method.*

import java.util.concurrent.Executors;

import groovy.json.JsonSlurper
import groovyx.net.http.*


class FilterMatureProjects {
	def baseFolder = new File("/opt/groovonomics/")
	def projectFolder = new File(baseFolder, "dataset/projects/metadata")
	def outputFile = new File(baseFolder, "dataset/projects/mature_projects.txt")
	
	def dataFolder = new File("/Users/carlosgsouza/workspace_gg/groovonomics/articles/aosd_2014/data/type_usage/classes")
	
	def wakeUp() {
		def projects = [:]
		
		def pool = Executors.newFixedThreadPool(5)
		projectFolder.eachFile { projectFile ->
			projects[projectFile.name] = new JsonSlurper().parseText(projectFile.text)
		}
		
		def matureProjects = projects.findAll{ it.value.loc.toInteger() > 2000 && it.value.commits.toInteger() > 100 && new File(dataFolder, it.key).exists()}.collect{it.key}
		
		if(outputFile.exists()) {
			outputFile.delete()
		}
		matureProjects.each {
			def name = it - ".json"
			outputFile << "$name\n"
		}
		
		println matureProjects.size()
	}
	
	public static void main(String[] args) {
		new FilterMatureProjects().wakeUp()
	}
}