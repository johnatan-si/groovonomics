import static groovyx.net.http.ContentType.*
import static groovyx.net.http.Method.*
import groovy.json.JsonBuilder
import groovy.json.JsonOutput
import groovyx.net.http.*


class GetUsersLanguages {
	def baseFolder = new File("/opt/groovonomics/")
	
	def basicAuth = new File(baseFolder, "conf/github.conf").text
	def gitHubClient = new RESTClient("https://api.github.com")
	def requestHeaders = ["Authorization":"Basic $basicAuth", "User-Agent": "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_5) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.65 Safari/537.31"]
	
	def projectListFile = new File(baseFolder, "dataset/projects/list.txt")
	
	File outputBaseFolder = new File(baseFolder, "data/users/")
	
	def shineOnYouCrazyDiamond() {
		def i = 0
		projectListFile.eachLine { projectName ->
			def (owner, name) = parseProjectName(projectName)
			def outputFile = new File(outputBaseFolder, "${owner}.json")
			
			if(!outputFile.exists()) {
				println "\n\n\n\n\n\n\n\n\n${i++}\n\n\n\n\n\n\n\n\n\n"
				
				def languages = getUserLanguages(owner)
				
				outputFile << languages.join("\n")
			}
		}
		
	}
	
	def getUserLanguages(user) {
		try {
			def response = gitHubClient.get(path: "/users/$user/repos", headers:requestHeaders, contentType:ContentType.JSON)
			return response.data*.language ?: []
		} catch(e) {
			e.printStackTrace()
			return []
		}
	}
	
	
	
	def parseProjectName(projectName) {
		def owner = projectName.substring(0, projectName.lastIndexOf("_"))
		def name = projectName.substring(projectName.lastIndexOf("_")+1)
		
		[owner, name]
	}
	
	public static void main(String[] args) {
		new DownloadGitHubMetadata().shineOnYouCrazyDiamond()
	}
}