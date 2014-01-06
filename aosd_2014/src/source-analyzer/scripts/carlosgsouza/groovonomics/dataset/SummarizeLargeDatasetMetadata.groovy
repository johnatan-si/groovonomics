import groovy.json.JsonBuilder
import groovy.json.JsonOutput
import groovy.json.JsonSlurper

def baseFolder = new File("/opt/groovonomics/dataset/projects/metadata/")

def allContributors = new HashSet()
def totalLoc = 0L
def totalNumberOfFiles = 0L
def totalSize = 0L
def numberOfProjects = 0

def i = 0

baseFolder.eachFile { file ->
	
	println i++
	
	if(file.name.endsWith("json") && file.name != "all.json"){
		def projectData = new JsonSlurper().parseText(file.text)
		
		if(!projectData.isPrivate) {
			allContributors.addAll projectData.contributors
			totalLoc += projectData.loc.toLong()
			totalNumberOfFiles += projectData.numberOfFiles.toLong()
			totalSize += projectData.totalSize.toLong()
			numberOfProjects++
		}
	}
}

def result = ["numberOfContributors":allContributors.size(), "totalLoc":totalLoc, "totalNumberOfFiles":totalNumberOfFiles, "totalSize":totalSize, "numberOfProjects":numberOfProjects]
def jsonText = JsonOutput.prettyPrint(new JsonBuilder(result).toString())
def allResultFile = new File(baseFolder, "all.json")
def w = allResultFile.newWriter() 
w.write jsonText
w.close()

