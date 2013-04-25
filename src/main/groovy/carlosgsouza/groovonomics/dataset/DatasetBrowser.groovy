package carlosgsouza.groovonomics.dataset;

import java.text.SimpleDateFormat;

public class DatasetBrowser {
	GitHubWebClient gitHubWebClient
	
	def FIRST_GROOVY_PROJECT_CREATION = new GregorianCalendar(2008, GregorianCalendar.MARCH, 31).time
	def today = new GregorianCalendar().time
	
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd")
	
	def getProjectsCreatedAt(String date) {
		gitHubWebClient.searchProjects(date, "groovy")
	}
	
	def listAllGroovyProjectsToDate() {
		def result = []
		(FIRST_GROOVY_PROJECT_CREATION..today).each {
			result.addAll gitHubWebClient.searchProjects(sdf.format(it), "groovy")
		}
		result
	}
}
