package carlosgsouza.groovonomics.dataset

import spock.lang.Specification;

class DatasetBrowserSpec extends Specification {

	def "should query all projects created on a given date"() {
		given:
		def aDate = "2010-11-26"
		
		and:
		def datasetBrowser = new DatasetBrowser()
		datasetBrowser.gitHubWebClient = Mock(GitHubWebClient)
		
		when:
		def projects = datasetBrowser.getProjectsCreatedAt(aDate)
		
		then:
		1 * datasetBrowser.gitHubWebClient.searchProjects(aDate, "groovy") >> ["owner1/project1", "owner2/project2"]
		
		and:
		projects == ["owner1/project1", "owner2/project2"] 
	}
	
	def "should query for projects created on every day since the first project was created until yesterday"() {
		given:
		def datasetBrowser = new DatasetBrowser()
		
		and:
		datasetBrowser.FIRST_GROOVY_PROJECT_CREATION = new GregorianCalendar(2009, GregorianCalendar.JANUARY, 1).time
		datasetBrowser.today = new GregorianCalendar(2010, GregorianCalendar.JANUARY, 1).time
		
		and:
		datasetBrowser.gitHubWebClient = Mock(GitHubWebClient)
		
		when:
		def projects = datasetBrowser.listAllGroovyProjectsToDate()
		
		then:
		365 * datasetBrowser.gitHubWebClient.searchProjects(_, "groovy") >> []
	}
	
	def "should work for leap years"() {
		given:
		def datasetBrowser = new DatasetBrowser()
		
		and:
		datasetBrowser.FIRST_GROOVY_PROJECT_CREATION = new GregorianCalendar(2012, GregorianCalendar.JANUARY, 1).time
		datasetBrowser.today = new GregorianCalendar(2013, GregorianCalendar.JANUARY, 1).time
		
		and:
		datasetBrowser.gitHubWebClient = Mock(GitHubWebClient)
		
		when:
		def projects = datasetBrowser.listAllGroovyProjectsToDate()
		
		then:
		366 * datasetBrowser.gitHubWebClient.searchProjects(_, "groovy") >> []
	}
	
	def "should return all projects found so far"() {
		given:
		def datasetBrowser = new DatasetBrowser()
		
		and:
		datasetBrowser.FIRST_GROOVY_PROJECT_CREATION = new GregorianCalendar(2010, GregorianCalendar.JANUARY, 1).time
		datasetBrowser.today = new GregorianCalendar(2010, GregorianCalendar.JANUARY, 3).time
		
		and:
		datasetBrowser.gitHubWebClient = Mock(GitHubWebClient)
		
		when:
		def projects = datasetBrowser.listAllGroovyProjectsToDate()
		
		then:
		datasetBrowser.gitHubWebClient.searchProjects("2010-01-01", "groovy") >> ["day1_owner1/project1"]
		datasetBrowser.gitHubWebClient.searchProjects("2010-01-02", "groovy") >> ["day2_owner1/project1", "day2_owner2/project2"]
		datasetBrowser.gitHubWebClient.searchProjects("2010-01-03", "groovy") >> ["day3_owner1/project1", "day3_owner1/project2"]
		
		and:
		projects == ["day1_owner1/project1", "day2_owner1/project1", "day2_owner2/project2", "day3_owner1/project1", "day3_owner1/project2"]
	}
	
	
}
