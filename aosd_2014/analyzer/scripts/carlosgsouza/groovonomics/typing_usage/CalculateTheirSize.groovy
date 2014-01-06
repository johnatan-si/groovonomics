import groovy.json.JsonBuilder
import groovy.json.JsonOutput
import carlosgsouza.groovonomics.ProjectInspector

def projectsFolder = new File("/Users/carlosgsouza/projects/")
//def projectsFolder = new File("/Users/carlosgsouza/Dropbox/UFMG/Mestrado/mes/groovonomics/src/test/resources/carlosgsouza/groovonomics/projects_folder/")
def dataFolder = new File("/Users/carlosgsouza/Dropbox/UFMG/Mestrado/mes/groovonomics/data")
def outputFolder = new File(dataFolder, "usage")
def overallResult = ["fields": ["s":0, "d":0], "methods" : ["s":0, "d":0]]

println "Starting at " + new Date()

projectsFolder.eachDir { projectFolder ->
	def id = projectFolder.name.toInteger()

	println "===== Processing $projectFolder.name ====="

	def projectInspector = new ProjectInspector(projectFolder)
	def projectData = projectInspector.getTypeSystemUsageData()

	println "Results for $projectFolder.name: $projectData"
		
	def jsonText = JsonOutput.prettyPrint(new JsonBuilder(projectData).toString())
	def outputFile = new File(outputFolder, "${projectFolder.name}.json")
	outputFile << jsonText

	overallResult["fields"]["s"] += projectData["fields"]["s"]
	overallResult["fields"]["d"] += projectData["fields"]["d"]
	overallResult["methods"]["s"] += projectData["methods"]["s"]
	overallResult["methods"]["d"] += projectData["methods"]["d"]
}

def jsonText = JsonOutput.prettyPrint(new JsonBuilder(overallResult).toString())
def allResultFile = new File(outputFolder, "all.json")
allResultFile << jsonText

println "Finishing at " + new Date()
