package carlosgsouza.groovonomics.typing_usage2
import static groovyx.net.http.ContentType.*
import static groovyx.net.http.Method.*
import groovy.json.JsonBuilder
import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import groovyx.net.http.*

import java.util.concurrent.Executors


class GetProjectCommitCount {
	JsonSlurper slurper = new JsonSlurper()
	
	def baseFolder = new File("/opt/groovonomics/")
	
	File projectMetadataFolder = new File(baseFolder, "dataset/projects/metadata/")
	File projectCommitsFolder = new File(baseFolder, "data/type_usage/commits/")
	File outputFolder = new File(baseFolder, "dataset/projects/metadata2/")
	
	def basicAuth = new File(baseFolder, "conf/github.conf").text
	def requestHeaders = ["Authorization":"Basic $basicAuth", "User-Agent": "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_5) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.65 Safari/537.31"]
	
	def countThemAll() {
		if(!outputFolder.exists()) {
			outputFolder.mkdirs()
		}
		
		def pool = Executors.newFixedThreadPool(10)
		projectMetadataFolder.eachFile { projectFile ->
			pool.submit {
				def outputFile = new File(outputFolder, projectFile.name)
				if(outputFile.exists()) {
					return
				}
				
				def metadata = slurper.parseText(projectFile.text)
				def commits = getAlreadyCountedCommits(projectFile)
				
				
				if(!commits) {
					def (owner, name) = parseProjectName(projectFile.name - ".json")
					commits = getCommitsIds(owner, name).size()
				} 
				
				metadata.commits = commits
				outputFile << JsonOutput.prettyPrint(new JsonBuilder(metadata).toString())
			}
		}
		
	}
	
	def getAlreadyCountedCommits(projectFile) {
		try {
			def commitsFile = new File(projectCommitsFolder, projectFile.name)
			return slurper.parseText(commitsFile.text).commits
		} catch(e) {
			return null
		}
	}
	
	def getCommitsIds(owner, name) {
		def result = []
		
		def firstUrl = "/repos/$owner/$name/commits?per_page=100"
		
		
		def log = new File("log.txt")
		
		def (nextUrl, ids) = requestCommitsIds(firstUrl)
		result.addAll ids
		
		while(nextUrl) {
			(nextUrl, ids) = requestCommitsIds(nextUrl)
			result.addAll ids
		}
		
		return result
	}
	
	def requestCommitsIds(url) {
		try {
			def gitHubClient = new RESTClient("https://api.github.com")
			def urlBuilder = new URIBuilder(url)
			
			def response = gitHubClient.get(path: urlBuilder.path, query:urlBuilder.query, headers:requestHeaders, contentType:ContentType.JSON)
			
			def next = extractNextLinkFromHeader(response)
			def ids = response?.data*.sha
			
			return [next, ids]
		} catch(e) {
			e.printStackTrace()
			return [null, []]
		}
	}
	
	def extractNextLinkFromHeader(response) {
		def nextLinkPattern = /<(.*)>; rel="next"/
		
		try {
			return (response.headers?.Link =~ nextLinkPattern)[0][1]
		} catch(e) {
			return null
		}
	}
	
	def parseProjectName(projectName) {
		def owner = projectName.substring(0, projectName.lastIndexOf("_"))
		def name = projectName.substring(projectName.lastIndexOf("_")+1)
		
		[owner, name]
	}
	
	public static void main(String[] args) {
		new GetProjectCommitCount().countThemAll()
	}
}