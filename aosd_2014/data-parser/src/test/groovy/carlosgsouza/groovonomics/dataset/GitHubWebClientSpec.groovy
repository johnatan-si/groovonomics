package carlosgsouza.groovonomics.dataset

import org.jsoup.Jsoup;

import groovy.util.slurpersupport.GPathResult;
import groovyx.net.http.HTTPBuilder;
import groovyx.net.http.RESTClient;
import spock.lang.Specification;

class GitHubWebClientSpec extends Specification {
	
	def load(fileName) {
		new JsoupWrapper().parse(new File("src/test/resources/carlosgsouza/groovonomics/dataset/", fileName).absolutePath)
	}
	
	def "should emulate and advanced search request made from the web browser"() {
		given:
		def gitHubWebClient = new GitHubWebClient()
		gitHubWebClient.jsoupWrapper = Mock(JsoupWrapper)
				
		when:
		def projects = gitHubWebClient.searchProjects("2010-11-26", "groovy")
		
		then:
		1 * gitHubWebClient.jsoupWrapper.parse("https://github.com/search?q=created%3A2010-11-26&type=Repositories&ref=searchresults&l=groovy") >> load("search_result_for_groovy_projects_created_at_2010-11-26.html")
		
		and:
		projects == ["mstine/exec-specs-geb-spock", "rsnape/CASCADE_zonal"] 
	}
	
	def "should list the projects of other pages in case there is pagination"() {
		given:
		def gitHubWebClient = new GitHubWebClient()
		gitHubWebClient.jsoupWrapper = Mock(JsoupWrapper)
				
		when:
		def projects = gitHubWebClient.searchProjects("2010-11-26", "groovy")
		
		then:
		1 * gitHubWebClient.jsoupWrapper.parse("https://github.com/search?q=created%3A2010-11-26&type=Repositories&ref=searchresults&l=groovy") >> load("search_result_with_more_than_one_page_1.html")
		
		and:
		1 * gitHubWebClient.jsoupWrapper.parse("https://github.com/search?l=groovy&p=2&q=created%3A2010-11-26&type=Repositories&ref=searchresults") >> load("search_result_with_more_than_one_page_2.html")
		
		and:
		projects == ["gotascii/hurl", "jsiarto/hello_github", "davidsmalley/extensionless_format", "mmower/platform", "schmidt/context_wiki", "JackDanger/simple_memoize", "mmower/mailtrap", "matthewford/merb-meet-aop", "jamis/net-ssh-gateway", "schmidt/contextr", "daikini/kato"]
	}
	
	def "should understand when GitHub is complaining about too many requests and wait longer"() {
		given:
		def gitHubWebClient = new GitHubWebClient()
		gitHubWebClient.jsoupWrapper = Mock(JsoupWrapper)
		gitHubWebClient.human = Mock(Human)
				
		when:
		def projects = gitHubWebClient.searchProjects("2010-11-26", "groovy")
		
		then:
		1 * gitHubWebClient.jsoupWrapper.parse("https://github.com/search?q=created%3A2010-11-26&type=Repositories&ref=searchresults&l=groovy") >> load("too_many_requests_page.html")
		
		and:
		1 * gitHubWebClient.human.thinkFor(60)
		
		and:
		1 * gitHubWebClient.jsoupWrapper.parse("https://github.com/search?q=created%3A2010-11-26&type=Repositories&ref=searchresults&l=groovy") >> load("search_result_for_groovy_projects_created_at_2010-11-26.html")
		
		and:
		projects == ["mstine/exec-specs-geb-spock", "rsnape/CASCADE_zonal"]
	}
	
	def "should not fail when no results are returned"() {
		given:
		def gitHubWebClient = new GitHubWebClient()
		gitHubWebClient.jsoupWrapper = Mock(JsoupWrapper)
		gitHubWebClient.human = Mock(Human)
				
		when:
		def projects = gitHubWebClient.searchProjects("2007-11-26", "groovy")
		
		then:
		1 * gitHubWebClient.jsoupWrapper.parse("https://github.com/search?q=created%3A2007-11-26&type=Repositories&ref=searchresults&l=groovy") >> load("no_results_page.html")
		
		and:
		projects == []
	}
}
