package carlosgsouza.groovonomics

import groovy.json.JsonBuilder;
import groovy.json.JsonOutput;
import groovy.json.JsonSlurper;
import groovyx.net.http.RESTClient

class QueryGroovyRepositories {
	
	private repositories
	
	public QueryGroovyRepositories(repositories) {
		this.repositories = repositories
	}
	
	static void main(args) {
		def repositoriesFile = new File("/Users/carlosgsouza/groovonomics/data/repositories.json")
		def repositories = new JsonSlurper().parseText(repositoriesFile.text)
		
		def query = new QueryGroovyRepositories(repositories)
		query.listCloneUrls()
	}
	
	def listCloneUrls() {
		def count = 1
		repositories.sort{ it.size }.each { r ->
			println "mkdir $count ; cd $count ; git clone git://github.com/$r.username/${r.name}.git ; cd .."
			count++
		}
	}
}
