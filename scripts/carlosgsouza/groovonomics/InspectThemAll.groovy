import groovy.io.FileType
import groovy.json.JsonBuilder
import groovy.json.JsonOutput

def projectsFolder = new File("/Users/carlosgsouza/projects/")
//def projectsFolder = new File("/Users/carlosgsouza/Dropbox/UFMG/Mestrado/mes/groovonomics/src/test/resources/carlosgsouza/groovonomics/projects_folder/")
def dataFolder = new File("/Users/carlosgsouza/Dropbox/UFMG/Mestrado/mes/groovonomics/data")

def result = [:]

projectsFolder.eachDir { projectFolder ->
	def id = projectFolder.name
	
	def projectData = ["lines":0, "files":0]

	println "===== Processing $projectFolder.name ====="

	projectFolder.eachFileRecurse(FileType.FILES) {
		if(it.name.endsWith(".groovy")) {
			it.eachLine { projectData["lines"]++ }
			projectData["files"]++
		}	
	}
	
	println projectData
	result[id] = projectData
}

def outputFolder = new File(dataFolder, "size")
def jsonText = JsonOutput.prettyPrint(new JsonBuilder(result).toString())
def allResultFile = new File(outputFolder, "size.json")
allResultFile << jsonText
