package carlosgsouza.groovonomics.dataset

import carlosgsouza.groovonomics.common.FileHandler;
import spock.lang.Specification;

class DatasetBrowserSpec extends Specification {

	def "should query for projects created on days 1-31 of a given month"() {
		given:
		def datasetBrowser = new DatasetBrowser(2009, 1)
		datasetBrowser.fileHandler = Mock(FileHandler)
		datasetBrowser.human = Mock(Human)
		
		and:
		datasetBrowser.gitHubWebClient = Mock(GitHubWebClient)
		
		when:
		datasetBrowser.listAllGroovyProjectsToDate()
		
		then:
		1 * datasetBrowser.gitHubWebClient.searchProjects("2009-01-01", "groovy") >> []
		
		and:
		29 * datasetBrowser.gitHubWebClient.searchProjects({ it ==~ /2009-01-[0-3][0-9]/ }, "groovy") >> []
		
		and:
		1 * datasetBrowser.gitHubWebClient.searchProjects("2009-01-31", "groovy") >> []
	}
	
	def "should write project owner/name to output file in order"() {
		given:
		def datasetBrowser = new DatasetBrowser(2010, 1)
		datasetBrowser.fileHandler = Mock(FileHandler)
		datasetBrowser.human = Mock(Human)
		
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
		
		and:
		28 * datasetBrowser.gitHubWebClient.searchProjects(_, "groovy") >> []
		0 * datasetBrowser.fileHandler.output(_)
	}
	
	def "should try loading the page 3 times before quitting"() {
		given:
		def datasetBrowser = new DatasetBrowser(2010, 1)
		datasetBrowser.fileHandler = Mock(FileHandler)
		datasetBrowser.human = Mock(Human)
		
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
		
		and: "shit happens again"
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
		
		and: "finish the month" 
		_ * datasetBrowser.gitHubWebClient.searchProjects(_ , "groovy") >> []
		_ * datasetBrowser.fileHandler.log(_)
	}
	
	def "should have a think time between requests"() {
		given:
		def datasetBrowser = new DatasetBrowser(2010, 1)
		datasetBrowser.fileHandler = Mock(FileHandler)
		datasetBrowser.human = Mock(Human)
		
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
		29 * datasetBrowser.gitHubWebClient.searchProjects(_, "groovy") >> []
		29 * datasetBrowser.human.think()
	}
	
	def "should format a date to github style"(date, year, month, day) {
		expect:
		date == new DatasetBrowser(year, month).formatDate(year, month, day)
		
		where:
		date			| year | month	| day
		"2009-01-01" 	| 2009 | 1		| 1 
		"2009-02-31"	| 2009 | 2		| 31
		"2009-09-01"	| 2009 | 9		| 1
		"2009-10-01" 	| 2009 | 10		| 1
		"2009-11-11" 	| 2009 | 11		| 11
		"2012-12-12" 	| 2012 | 12		| 12   
	}
	
}
