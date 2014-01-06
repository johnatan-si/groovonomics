package carlosgsouza.groovonomics.typing_usage;

import java.util.concurrent.Callable
import java.util.concurrent.Executors
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit


public class ClassDataAnalyser {
	
	def projectsFolder = new File("/Users/carlosgsouza/projects/")
	def dataFolder = new File("/Users/carlosgsouza/Dropbox/UFMG/Mestrado/mes/groovonomics/data/classes")
	def threadCount = 3
	def projectsPool
	
	ClassDataAnalyser() {
		
	}
	
	def inspectThemAll() {
		def startTime = System.currentTimeMillis()
		
		projectsPool = createAPoolOfProjects()
		
		def ThreadPoolExecutor threadPool = Executors.newFixedThreadPool(threadCount)
		try {
			projectsPool.each { projectFolder ->
				threadPool.submit ({
					processProject projectFolder
				} as Callable)
			}
			
		}catch(e) {
			e.printStackTrace()
		}
		finally {
			threadPool.shutdown()
			threadPool.awaitTermination(3000, TimeUnit.SECONDS)
		}
		
		println "Finished! Total time taken: ${System.currentTimeMillis() - startTime}"
		
	}
	
	def processProject(projectFolder) {
		try {
			println "===== Processing $projectFolder.name ====="
			
			def projectInspector = new ProjectInspector(projectFolder)
			def projectData = projectInspector.getTypeSystemUsageData()
			
			def outputFolder = new File(dataFolder, "${projectData.id}.json")
			outputFolder << projectData.toJson()
		} catch(e) {
			println "Something went wront with this project: $projectName"
		}
	}

	def createAPoolOfProjects() {
		def projectsPool = [].asSynchronized()
		projectsFolder.eachDir { projectFolder ->
			projectsPool.add projectFolder
		}
		return projectsPool
	}
	
	public static void main(String[] args) {
		new ClassDataAnalyser().inspectThemAll()
	}
}
