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
			def projectData = projectDataFactory.fromJsonFile(projectDataFile)
			def projectAgregate = projectData.agregate()
			
			new File(outputFolder, projectDataFile.name) << projectAgregate
			
			allProjectsAgregate += projectAgregate
		}
		new File(outputFolder, "all.json") << allProjectsAgregate
	}
	
	def agregateOverall() {
		def overall = new DeclarationCount()
		
		def all = new ClassDataFactory().fromJsonFile new File(outputFolder, "all.json")
		overall += all.publicMethodReturn
		overall += all.privateMethodReturn
		overall += all.protectedMethodReturn
		overall += all.publicField
		overall += all.privateField
		overall += all.protectedField
		overall += all.publicMethodParameter
		overall += all.privateMethodParameter
		overall += all.protectedMethodParameter
		overall += all.pureTypeSystemPublicMethods
		overall += all.pureTypeSystemPrivateMethods
		overall += all.pureTypeSystemProtectedMethods
		overall += all.publicConstructorParameter
		overall += all.privateConstructorParameter
		overall += all.protectedConstructorParameter
		overall += all.pureTypeSystemPublicConstructors
		overall += all.pureTypeSystemPrivateConstructors
		overall += all.pureTypeSystemProtectedConstructors
		overall += all.localVariable
		
		new File(outputFolder, "overall.json") << overall
	}
	
	public static void main(String[] args) {
		new ProjectDataAgregator().agregateOverall()
	}
}
