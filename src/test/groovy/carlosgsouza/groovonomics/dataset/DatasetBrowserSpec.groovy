package carlosgsouza.groovonomics.dataset

import carlosgsouza.groovonomics.common.FileHandler;
import spock.lang.Specification;

class DatasetBrowserSpec extends Specification {

	def "should query for projects created on every day since the first project was created until yesterday"() {
		given:
		def datasetBrowser = new DatasetBrowser()
		datasetBrowser.fileHandler = Mock(FileHandler)
		datasetBrowser.human = Mock(Human)
		
		and:
		datasetBrowser.FIRST_GROOVY_PROJECT_CREATION = new GregorianCalendar(2009, GregorianCalendar.JANUARY, 1).time
		datasetBrowser.today = new GregorianCalendar(2010, GregorianCalendar.JANUARY, 1).time
		
		and:
		datasetBrowser.gitHubWebClient = Mock(GitHubWebClient)
		
		when:
		datasetBrowser.listAllGroovyProjectsToDate()
		
		then:
		365 * datasetBrowser.gitHubWebClient.searchProjects(_, "groovy") >> []
	}
	
	def "should work for leap years"() {
		given:
		def datasetBrowser = new DatasetBrowser()
		datasetBrowser.fileHandler = Mock(FileHandler)
		datasetBrowser.human = Mock(Human)
		
		and:
		datasetBrowser.FIRST_GROOVY_PROJECT_CREATION = new GregorianCalendar(2012, GregorianCalendar.JANUARY, 1).time
		datasetBrowser.today = new GregorianCalendar(2013, GregorianCalendar.JANUARY, 1).time
		
		and:
		datasetBrowser.gitHubWebClient = Mock(GitHubWebClient)
		
		when:
		datasetBrowser.listAllGroovyProjectsToDate()
		
		then:
		366 * datasetBrowser.gitHubWebClient.searchProjects(_, "groovy") >> []
	}
	
	def "should write project owner/name to output file in order"() {
		given:
		def datasetBrowser = new DatasetBrowser()
		datasetBrowser.fileHandler = Mock(FileHandler)
		datasetBrowser.human = Mock(Human)
		
		and:
		datasetBrowser.FIRST_GROOVY_PROJECT_CREATION = new GregorianCalendar(2010, GregorianCalendar.JANUARY, 1).time
		datasetBrowser.today = new GregorianCalendar(2010, GregorianCalendar.JANUARY, 3).time
		
		and:
		datasetBrowser.gitHubWebClient = Mock(GitHubWebClient)
		
		when:
		datasetBrowser.listAllGroovyProjectsToDate()
		
		then:
		1 * datasetBrowser.gitHubWebClient.searchProjects("2010-01-01", "groovy") >> ["day1_owner1/project1"]
		1 * datasetBrowser.fileHandler.output("day1_owner1/project1")
		
		and:
		1 * datasetBrowser.gitHubWebClient.searchProjects("2010-01-02", "groovy") >> ["day2_owner1/project1", "day2_owner2/project2"]
		1 * datasetBrowser.fileHandler.output("day2_owner1/project1")
		1 * datasetBrowser.fileHandler.output("day2_owner2/project2")
		
		and:
		1 * datasetBrowser.gitHubWebClient.searchProjects("2010-01-03", "groovy") >> ["day3_owner1/project1", "day3_owner1/project2"]
		1 * datasetBrowser.fileHandler.output("day3_owner1/project1")
		1 * datasetBrowser.fileHandler.output("day3_owner1/project2")
	}
	
	def "should try loading the page 3 times before quitting"() {
		given:
		def datasetBrowser = new DatasetBrowser()
		datasetBrowser.fileHandler = Mock(FileHandler)
		datasetBrowser.human = Mock(Human)
		
		and:
		datasetBrowser.FIRST_GROOVY_PROJECT_CREATION = new GregorianCalendar(2010, GregorianCalendar.JANUARY, 1).time
		datasetBrowser.today = new GregorianCalendar(2010, GregorianCalendar.JANUARY, 3).time
		
		and:
		datasetBrowser.gitHubWebClient = Mock(GitHubWebClient)
		
		when:
		datasetBrowser.listAllGroovyProjectsToDate()
		
		then:
		1 * datasetBrowser.gitHubWebClient.searchProjects("2010-01-01", "groovy") >> { throw new RuntimeException() }
		1 * datasetBrowser.fileHandler.logError(_)
		
		and: "try again"
		1 * datasetBrowser.gitHubWebClient.searchProjects("2010-01-01", "groovy") >> { throw new RuntimeException() }
		1 * datasetBrowser.fileHandler.logError(_)
		
		and: "try again again"
		1 * datasetBrowser.gitHubWebClient.searchProjects("2010-01-01", "groovy") >> { throw new RuntimeException() }
		1 * datasetBrowser.fileHandler.logError(_)
		
		and: "log one final error message"
		1 * datasetBrowser.fileHandler.logError(_)
		
		and: "this goes well"
		1 * datasetBrowser.gitHubWebClient.searchProjects("2010-01-02", "groovy") >> ["day2_owner1/project1", "day2_owner2/project2"]
		
		and: "shit happens agains"
		1 * datasetBrowser.gitHubWebClient.searchProjects("2010-01-03", "groovy") >> { throw new RuntimeException() }
		1 * datasetBrowser.fileHandler.logError(_)
		
		and: "try again"
		1 * datasetBrowser.gitHubWebClient.searchProjects("2010-01-03", "groovy") >> { throw new RuntimeException() }
		1 * datasetBrowser.fileHandler.logError(_)
		
		and: "try again again"
		1 * datasetBrowser.gitHubWebClient.searchProjects("2010-01-03", "groovy") >> { throw new RuntimeException() }
		1 * datasetBrowser.fileHandler.logError(_)
		
		and: "log one final error message"
		1 * datasetBrowser.fileHandler.logError(_)
	}
	
	def "should have a think time between requests"() {
		given:
		def datasetBrowser = new DatasetBrowser()
		datasetBrowser.fileHandler = Mock(FileHandler)
		datasetBrowser.human = Mock(Human)
		
		and:
		datasetBrowser.FIRST_GROOVY_PROJECT_CREATION = new GregorianCalendar(2010, GregorianCalendar.JANUARY, 1).time
		datasetBrowser.today = new GregorianCalendar(2010, GregorianCalendar.JANUARY, 3).time
		
		and:
		datasetBrowser.gitHubWebClient = Mock(GitHubWebClient)
		
		when:
		datasetBrowser.listAllGroovyProjectsToDate()
		
		then:
		1 * datasetBrowser.gitHubWebClient.searchProjects("2010-01-01", "groovy") >> []
		1 * datasetBrowser.human.think()
		
		and:
		1 * datasetBrowser.gitHubWebClient.searchProjects("2010-01-02", "groovy") >> []
		1 * datasetBrowser.human.think()
		
		and:
		1 * datasetBrowser.gitHubWebClient.searchProjects("2010-01-03", "groovy") >> []
		1 * datasetBrowser.human.think()
	}
	
}
