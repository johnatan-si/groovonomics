package carlosgsouza.groovonomics;

import groovy.json.JsonSlurper



public class ProjectDataAgregator {
	
	def classDataFolder = new File("/Users/carlosgsouza/Dropbox/UFMG/Mestrado/mes/groovonomics/data/some_classes")
	def outputFolder = new File("/Users/carlosgsouza/Dropbox/UFMG/Mestrado/mes/groovonomics/data/class_agregate")
	def sizeOfProjects = new JsonSlurper().parseText(new File("/Users/carlosgsouza/Dropbox/UFMG/Mestrado/mes/groovonomics/data/size/size.json").text)
	
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
	
	def agreageteBySize() {
		def datasetAgreagete = new ClassData()
		
		def agregateBuckets = []
		12.times {
			agregateBuckets.add new AgregateProjectData()
			agregateBuckets.id = it
		}
		
		classDataFolder.eachFile { projectDataFile ->
			def projectData = projectDataFactory.fromJsonFile(projectDataFile)
			def size = getSizeOfProject(projectData)
			
			def projectAgregate = projectData.agregate()
			def bucketIndex = getBucketForSize(size)
			
			agregateBuckets[bucketIndex].data += projectAgregate
		}
		
		new File(outputFolder, "agregate_by_size.json") << agregateBuckets
	}
	
	def getBucketForSize(size) {
		
	}
	
	def getSizeOfProject(projectDataFile) {
		
	}
	
	def agreageteScriptsAndClasses() {
		def datasetAgreagete = new ClassData()
		
		def allScriptsAgregate = new DeclarationCount()
		def allClassesAgregate = new DeclarationCount()
			
		classDataFolder.eachFile { projectDataFile ->
			def projectData = projectDataFactory.fromJsonFile(projectDataFile)
			def projectScriptsAgregate = projectData.agregateScripts()
			def projectClassesAgregate = projectData.agregateClasses()
			
			allScriptsAgregate += projectScriptsAgregate
			allClassesAgregate += projectClassesAgregate
		}
		new File(outputFolder, "scripts.json") << allScriptsAgregate
		new File(outputFolder, "classes.json") << allClassesAgregate
	}
	
	def agreageteTestAndFuncionalClasses() {
		def datasetAgreagete = new ClassData()
		
		def allTestAgregate = new DeclarationCount()
		def allFunctionalAgregate = new DeclarationCount()
			
		classDataFolder.eachFile { projectDataFile ->
			def projectData = projectDataFactory.fromJsonFile(projectDataFile)
			
			if(projectData.hasTests()) {
				def projectTestAgregate = projectData.agregateTestClasses()
				def projectFunctionalAgregate = projectData.agregateFunctionalClasses()
				
				allTestAgregate += projectTestAgregate
				allFunctionalAgregate += projectFunctionalAgregate
			}
		}
		new File(outputFolder, "test_classes.json") << allTestAgregate
		new File(outputFolder, "functional_classes.json") << allFunctionalAgregate
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
		overall += all.publicConstructorParameter
		overall += all.privateConstructorParameter
		overall += all.protectedConstructorParameter
		overall += all.localVariable
		
		new File(outputFolder, "overall.json") << overall
	}
	
	public static void main(String[] args) {
		new ProjectDataAgregator().agreageteTestAndFuncionalClasses()
	}
}
