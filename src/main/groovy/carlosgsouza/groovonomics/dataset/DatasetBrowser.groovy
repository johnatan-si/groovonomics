package carlosgsouza.groovonomics.dataset;

import java.text.SimpleDateFormat

import carlosgsouza.groovonomics.common.FileHandler

public class DatasetBrowser {
	GitHubWebClient gitHubWebClient
	FileHandler fileHandler
	Human human
	
	def FIRST_GROOVY_PROJECT_CREATION = new GregorianCalendar(2013, GregorianCalendar.APRIL, 20).time
//	def FIRST_GROOVY_PROJECT_CREATION = new GregorianCalendar(2008, GregorianCalendar.MARCH, 31).time
	def today = new GregorianCalendar().time
	
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd")
	
	public DatasetBrowser() {
		this(".")
	}
	
	public DatasetBrowser(baseDir) {
		fileHandler = new FileHandler(baseDir)
		gitHubWebClient = new GitHubWebClient(fileHandler)
		human = new Human(fileHandler)
	}
	
	def listProjectsCreatedAt(Date date) {
		def dateStr = sdf.format(date)
		fileHandler.log(dateStr)
		
		int attempt = 1
		
		while(attempt <= 3) {
			try {
				def projects = gitHubWebClient.searchProjects(dateStr, "groovy")
				projects.each { fileHandler.output(it) }
				
				break
			} catch(e) {
				fileHandler.logError("$dateStr | ${attempt} / 3 | $e.message")
				e.printStackTrace()
				
				attempt++
				
				if(attempt > 3) {
					fileHandler.logError("$dateStr | It wasn't possible to get the projects today")
				} else {
					human.thinkFor(60)
				}
			}
		}
	}
	
	def listAllGroovyProjectsToDate() {
		(FIRST_GROOVY_PROJECT_CREATION..today).each {
			listProjectsCreatedAt it
			human.think()
		}
	}
	
	public static void main(String[] args) {
		new DatasetBrowser(args[0]).listAllGroovyProjectsToDate()
	}
}
