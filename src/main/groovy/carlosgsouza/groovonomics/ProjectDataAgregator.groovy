package carlosgsouza.groovonomics;

import groovy.json.JsonSlurper



public class ProjectDataAgregator {
	
	def classDataFolder = new File("/Users/carlosgsouza/Dropbox/UFMG/Mestrado/mes/groovonomics/data/classes")
	def outputFolder = new File("/Users/carlosgsouza/Dropbox/UFMG/Mestrado/mes/groovonomics/data/class_agregate")
	
	def projectDataFactory = new ProjectDataFactory()
	
	def agreageteThemAll() {
		def datasetAgreagete = new ClassData()
		
		def allProjectsAgregate = new ClassData()
		classDataFolder.eachFile { projectDataFile ->
			def projectAgregate = projectDataFactory.fromJsonFile(projectDataFile)
			new File(outputFolder, projectDataFile.name) << projectAgregate
			
			allProjectsAgregate += projectAgregate
		}
		new File(outputFolder, "all.json") << projectAgregate
	}
	
	public static void main(String[] args) {
		new ProjectDataAgregator().agreageteThemAll()
	}
}
