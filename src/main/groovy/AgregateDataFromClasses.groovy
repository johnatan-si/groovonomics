import groovy.json.JsonSlurper
import carlosgsouza.groovonomics.typing_usage.AgregateProjectData
import carlosgsouza.groovonomics.typing_usage.ClassData
import carlosgsouza.groovonomics.typing_usage.ClassDataFactory
import carlosgsouza.groovonomics.typing_usage.DeclarationCount
import carlosgsouza.groovonomics.typing_usage.ProjectDataFactory

public class AgregateDataFromClasses {
	
	def classDataFolder = new File("/opt/groovonomics/data/type_usage/class/")
	
	File projectsFolder = new File("/opt/groovonomics/data/type_usage/project/")
	File agregateFolder = new File("/opt/groovonomics/data/type_usage/agregate/")
	
	def projectDataFactory = new ProjectDataFactory()
	
	public AgregateDataFromClasses() {
		projectsFolder.deleteDir()
		projectsFolder.mkdirs()
		
		agregateFolder.deleteDir()
		agregateFolder.mkdirs()
	}
	
	def agregeteThemAll() {
		def allProjectsAgregate = new ClassData()
		classDataFolder.eachFile { projectDataFile ->
			try {
				if(projectDataFile.text.size() == 0) {
					println projectDataFile
				}
				
				def projectData = projectDataFactory.fromJsonFile(projectDataFile)
				def projectAgregate = projectData.agregate()
				
				new File(projectsFolder, projectDataFile.name) << projectAgregate
				
				allProjectsAgregate += projectAgregate
			} catch(e) {
				println "${projectDataFile.name}\t$e.message"
			}
		}
		new File(agregateFolder, "all.json") << allProjectsAgregate
	}
	
	def agregeteScriptsAndClasses() {
		def datasetagregete = new ClassData()
		
		def allScriptsAgregate = new DeclarationCount()
		def allClassesAgregate = new DeclarationCount()
			
		classDataFolder.eachFile { projectDataFile ->
			def projectData = projectDataFactory.fromJsonFile(projectDataFile)
			def projectScriptsAgregate = projectData.agregateScripts()
			def projectClassesAgregate = projectData.agregateClasses()
			
			allScriptsAgregate += projectScriptsAgregate
			allClassesAgregate += projectClassesAgregate
		}
		new File(agregateFolder, "scripts.json") << allScriptsAgregate
		new File(agregateFolder, "classes.json") << allClassesAgregate
	}
	
	def agregeteTestAndFuncionalClasses() {
		def datasetagregete = new ClassData()
		
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
		new File(agregateFolder, "test_classes.json") << allTestAgregate
		new File(agregateFolder, "functional_classes.json") << allFunctionalAgregate
	}
	
	def agregateOverall() {
		def overall = new DeclarationCount()
		
		def all = new ClassDataFactory().fromJsonFile new File(agregateFolder, "all.json")
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
		
		new File(agregateFolder, "overall.json") << overall
	}
	
	public static void main(String[] args) {
		def agregator = new AgregateDataFromClasses()
		agregator.agregeteThemAll()
		agregator.agregateOverall()
		agregator.agregeteScriptsAndClasses()
		agregator.agregeteTestAndFuncionalClasses()
	}
}
