package carlosgsouza.groovonomics.typing_usage2
import static groovyx.net.http.ContentType.*
import static groovyx.net.http.Method.*
import groovy.json.JsonBuilder
import groovy.json.JsonOutput
import groovyx.net.http.*


class GetFileCommitCount {
	def baseFolder = new File("/opt/groovonomics/")
	File projectListFile = new File(baseFolder, "dataset/projects/top5p.txt")
	File outputFolder = new File(baseFolder, "data/type_usage/commits/")
	
	def basicAuth = new File(baseFolder, "conf/github.conf").text
	def gitHubClient = new RESTClient("https://api.github.com")
	def requestHeaders = ["Authorization":"Basic $basicAuth", "User-Agent": "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_5) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.65 Safari/537.31"]
	
	
	def countThemAll() {
//		outputFolder.deleteDir()
		if(!outputFolder.exists()) {
			outputFolder.mkdirs()
		}
		
		projectListFile.eachLine { projectName ->
			def outputFile = new File(outputFolder, "${projectName}.json")
			if(outputFile.exists()) {
				return
			}
			
			def (owner, name) = parseProjectName(projectName)
			
			def commits = getCommitsIds(owner, name)
			
			def file_commits = [:]
			
			if(commits.size() >= 100) {
				commits.each { id ->
					def files = getCommitFiles(owner, name, id)
					
					files.findAll{it.endsWith(".groovy")}.each { file ->  
						file_commits[file] = file_commits[file] ?: 0
						file_commits[file]++
					}
				}
			}
			def output = [commits:commits.size(), file_commits:file_commits]
			
			outputFile << JsonOutput.prettyPrint(new JsonBuilder(output).toString())
		}
		
	}
	
	def getCommitFiles(owner, name, id) {
		try {
			// Throttle down the requests so we don't exceed the hourly limit			
			// Thread.sleep(720)
			return gitHubClient.get(path: "/repos/$owner/$name/commits/$id", headers:requestHeaders, contentType:ContentType.JSON)?.data?.files*.filename
		} catch(e) {
			e.printStackTrace()
			return []
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
		new GetFileCommitCount().countThemAll()
	}
}