package carlosgsouza.groovonomics

import groovy.json.JsonBuilder;
import groovy.json.JsonOutput;
import groovy.json.JsonSlurper;
import groovyx.net.http.RESTClient

class GetGroovyRepositories {
	static void main(args) {
		def outputFolder = new File("/Users/carlosgsouza/groovonomics/data/")
		outputFolder.mkdir()
		
		def gitHubClient = new RESTClient("https://api.github.com/")
		
		def allRepositories = []
		
		(1..19).each { page ->
			println "Fetching Content from page $page"
			
			def repositoriesJSON = gitHubClient.get(path: "legacy/repos/search/groovy", query: ["language": "groovy", "start_page": page]).data.toString()
			def repositories = new JsonSlurper().parseText(repositoriesJSON).repositories
			allRepositories.addAll(repositories)
		}
		
		def masterPublicProjects = []
		
		allRepositories.each { r ->
			def isPrivate = r."private".toBoolean()
			def isFork = r."fork".toBoolean()
			
			if(!isFork && !isPrivate) {
				if(r.description && r.description.contains("\"")) {
					r.description = r.description.replace("\"", '\'')
				}
				masterPublicProjects.add r
			}
		}
		
		def jsonText = JsonOutput.prettyPrint(new JsonBuilder(masterPublicProjects).toString())
		def repositoriesFile = new File(outputFolder, "repositories.json")
		
		repositoriesFile << jsonText
	}
	
}
