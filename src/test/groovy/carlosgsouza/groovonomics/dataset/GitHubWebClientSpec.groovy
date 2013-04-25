package carlosgsouza.groovonomics.dataset

import org.jsoup.Jsoup;

import groovy.util.slurpersupport.GPathResult;
import groovyx.net.http.HTTPBuilder;
import groovyx.net.http.RESTClient;
import spock.lang.Specification;

class GitHubWebClientSpec extends Specification {

	def "should emulate and advanced search request made from the web browser"() {
		given:
		def gitHubWebClient = new GitHubWebClient()
				
		and:
		def aDate = "2010-11-26"
		
		and:
		def jsoupSpy = GroovySpy(Jsoup, global:true)
		
		when:
		def projects = gitHubWebClient.searchProjects(aDate, "groovy")
		
		then:
		1 * Jsoup.connect("https://github.com/search?q=created%3A2010-11-26&type=Repositories&ref=searchresults&l=groovy")
		
		and:
		projects == ["mstine/exec-specs-geb-spock", "rsnape/CASCADE_zonal"] 
	}
}
