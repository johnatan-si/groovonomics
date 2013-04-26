package carlosgsouza.groovonomics.dataset

import org.jsoup.nodes.Document

class GitHubWebClient {
	
	JsoupWrapper jsoupWrapper
	Human human
	
	Document currentDoc
	
	public GitHubWebClient(fileHandler) {
		jsoupWrapper = new JsoupWrapper()
		human = new Human(fileHandler)
	}
	
	public GitHubWebClient() {
	}
	
	def searchProjects(String date, String language) {
		currentDoc = null
		def projects = []
		
		projects.addAll(getProjectsFromPage("https://github.com/search?q=created%3A$date&type=Repositories&ref=searchresults&l=$language"))
		
		while(linkToNextPage) {
			projects.addAll(getProjectsFromPage(linkToNextPage))
		}
		
		return projects
	}
	
	def getLinkToNextPage() {
		currentDoc.getElementsByClass("next_page")?.attr("href")
	}
	
	def getProjectsFromPage(url) {
		def result = []
		
		currentDoc = jsoupWrapper.parse(url)
		if(currentDoc.getElementsByTag("h1")?.text() ==~ /Whoa there!/) {
			human.thinkFor(60)
			currentDoc = jsoupWrapper.parse(url)
		}
		
		def linksToStargazers = currentDoc.getElementsByClass("public")*.getElementsByTag("a")*.attr("href")
		
		linksToStargazers.each { 
			def owner_project = it.substring( 0, it.lastIndexOf("/")) //remove the "/stargazers" part of the url
			
			if(owner_project.startsWith("http")) { // if the url is not relative, remove the protocol and host
				def baseUrlLastIndex = owner_project.lastIndexOf("github.com/") + "github.com/".size()
				owner_project = owner_project.substring(baseUrlLastIndex)	
			}
			
			result.add owner_project
		}
		
		result
	}
}
