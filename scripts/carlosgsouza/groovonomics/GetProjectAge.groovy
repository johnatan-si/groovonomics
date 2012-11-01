import groovy.json.JsonBuilder
import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import groovyx.net.http.RESTClient

def authHeader = ["Authorization":"Basic "]
def gitHubClient = new RESTClient("https://api.github.com/")

def repositories = new JsonSlurper().parseText(new File("/Users/carlosgsouza/groovonomics/data/repositories.json").text)
def result = []

def i=1
def size=repositories.size()
repositories.each { r ->
	println "Getting data for repository ${i}/$size"
	i++
	
	def repoData = [:]
	
	def lastCommitDateText = gitHubClient.get(path:"/repos/$r.owner/$r.name/commits", headers:authHeader).data[0].commit.committer.date

	def lastCommitDate = Date.parse("yyyy-MM-dd", lastCommitDateText)
	def created = Date.parse("yyyy-MM-dd", r.created)
	
	repoData["age"] = lastCommitDate - created 
	repoData["owner"] = r.owner
	repoData["name"] = r.name
	
	result.add repoData
}

def jsonText = JsonOutput.prettyPrint(new JsonBuilder(result).toString())
def resultFile = new File("/Users/carlosgsouza/groovonomics/data/age.json")
resultFile << jsonText