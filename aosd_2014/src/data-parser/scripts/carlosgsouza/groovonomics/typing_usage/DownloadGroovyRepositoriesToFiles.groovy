package carlosgsouza.groovonomics

import groovy.json.JsonBuilder;


import groovy.json.JsonOutput;
import groovy.json.JsonSlurper;
import groovyx.net.http.RESTClient

/**
 * This will download all projects and save them in individual files by projectName/author
 * 
 * Notice that different projects may have the same name. So this script wont't work as expected
 * 
 * @author carlosgsouza
 *
 */
@Deprecated
class DownloadGroovyRepositoriesToFiles {
	static void main(args) {
		def outputFolder = new File("/Users/carlosgsouza/groovonomics/data/repositories")
		outputFolder.mkdir()
		
		def gitHubClient = new RESTClient("https://api.github.com/")
		
		def allRepositories = []
		
		(1..19).each { page ->
			println "Fetching Content from page $page"
			
			def repositoriesJSON = gitHubClient.get(path: "legacy/repos/search/groovy", query: ["language": "groovy", "start_page": page]).data.toString()
			def repositories = new JsonSlurper().parseText(repositoriesJSON).repositories
			allRepositories.addAll(repositories)
		}
		
		def allResults = [:]
		
		allRepositories.each { r ->
			def projectName = r.name
			def projectOwner = r.owner
			def privateProject = r."private".toBoolean()
			
			if(!allResults[projectName] & !privateProject) {
				allResults[projectName] = [:]
			}
	
			allResults[projectName][projectOwner] = r
		}
		
		def count = 1
		def total = allResults.size()
		
		allResults.each { name, resultsWithSameName ->
			println "Saving results for project $count/$total"
			count++
			
			def projectFolder = new File(outputFolder, name)
			projectFolder.mkdir()
	
			resultsWithSameName.each { owner, result ->
				def projectByOwnerFolder = new File(projectFolder, owner)
				projectByOwnerFolder.mkdir()
	
				def repositoryFile = new File (projectByOwnerFolder, "repository.json")
				repositoryFile << JsonOutput.prettyPrint(new JsonBuilder(result).toString()) 
			}
		}
		
	}
	
}
