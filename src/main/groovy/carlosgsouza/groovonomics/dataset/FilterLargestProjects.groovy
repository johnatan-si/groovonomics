import static groovyx.net.http.ContentType.*
import static groovyx.net.http.Method.*
import groovy.json.JsonSlurper
import groovyx.net.http.*


class FilterLargestProjects {
	def baseFolder = new File("/opt/groovonomics/dataset/projects/")
	def projectFolder = new File(baseFolder, "metadata")
	def outputFile = new File(baseFolder, "top5p.txt")
	
	def wakeUp() {
		
		def projects = [:]
		projectFolder.eachFile { projectFile ->
			projects[projectFile.name - ".json"] = new JsonSlurper().parseText(projectFile.text).loc.toInteger()
		}
		
		def sortedProjects = projects.sort{it.value}
		sortedProjects.each { name, size ->
			if(size > 4000) {
				outputFile << "$name\n"
			}
		}
	}
	
	public static void main(String[] args) {
		new FilterLargestProjects().wakeUp()
	}
}