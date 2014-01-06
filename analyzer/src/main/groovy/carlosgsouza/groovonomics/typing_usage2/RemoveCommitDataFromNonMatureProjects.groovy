package carlosgsouza.groovonomics.typing_usage2
import static groovyx.net.http.ContentType.*
import static groovyx.net.http.Method.*
import groovy.json.JsonBuilder
import groovy.json.JsonOutput
import groovy.json.JsonSlurper;
import groovyx.net.http.*


class RemoveCommitDataFromNonMatureProjects {
	File commitsFolder = new File("/opt/groovonomics/data/type_usage/commits/")
	JsonSlurper slurper = new JsonSlurper()
	
	def dataFolder = new File("/Users/carlosgsouza/workspace_gg/groovonomics/articles/aosd_2014/data/type_usage/classes")
	
	def killTheYoungOnes() {
		def i = 0
		
		commitsFolder.eachFile { commitsFile ->
			def project = slurper.parseText(commitsFile.text)
			if(project.commits.toInteger() < 100 || !new File(dataFolder, commitsFile.name).exists()) {
				commitsFile.delete()
			}
		}
	}
	
	public static void main(String[] args) {
		new RemoveCommitDataFromNonMatureProjects().killTheYoungOnes()
	}
}