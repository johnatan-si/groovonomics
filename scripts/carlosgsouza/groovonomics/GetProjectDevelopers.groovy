import groovy.json.JsonBuilder
import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import groovyx.net.http.RESTClient

def authHeader = ["Authorization":"Basic "]
def gitHubClient = new RESTClient("https://api.github.com/")

def repositories = new JsonSlurper().parseText(new File("/Users/carlosgsouza/groovonomics/data/repositories.json").text)

def developersSet = new HashSet<String>()

def i=1
def size=repositories.size()
repositories.each { r ->
	println "Getting data for repository ${i}/$size"
	i++
	
	def devIds = gitHubClient.get(path:"/repos/$r.owner/$r.name/contributors", headers:authHeader).data*.id
	developersSet.addAll(devIds)
}

println developersSet.size()