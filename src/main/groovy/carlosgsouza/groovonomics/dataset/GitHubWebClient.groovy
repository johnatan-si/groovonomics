package carlosgsouza.groovonomics.dataset

import org.jsoup.Jsoup;

import groovyx.net.http.ContentType;
import groovyx.net.http.HTTPBuilder

class GitHubWebClient {
	
	def searchProjects(String date, String language) {
		def doc = Jsoup.connect("https://github.com/search?q=created%3A$date&type=Repositories&ref=searchresults&l=$language").get()
		
		def linksToStargazers = doc.getElementsByClass("public")*.getElementsByTag("a")*.attr("href")
		
		linksToStargazers.collect { it.substring( 0, it.lastIndexOf("/") ) }
	}
}
