import groovy.json.JsonBuilder
import groovy.json.JsonOutput
import groovy.json.JsonSlurper

def baseFolder = new File("/opt/groovonomics/dataset/projects/metadata/")

def allContributors = new HashSet()
def totalLoc = 0L
def totalNumberOfFiles = 0L
def totalSize = 0L


baseFolder.eachFile { file ->
	if(file.name.endsWith("json") && file.name != "all.json"){
		def projectData = new JsonSlurper().parseText(file.text)
		
		if(!projectData.isPrivate) {
			allContributors.addAll projectData.contributors
			totalLoc += projectData.loc.toLong()
			totalNumberOfFiles += projectData.numberOfFiles.toLong()
			totalSize += projectData.totalSize.toLong()
		}
	}
}

def result = ["numberOfContributors":allContributors.size(), "totalLoc":totalLoc, "totalNumberOfFiles":totalNumberOfFiles, "totalSize":totalSize]
def jsonText = JsonOutput.prettyPrint(new JsonBuilder(result).toString())
def allResultFile = new File(baseFolder, "all.json")
def w = allResultFile.newWriter() 
w.write jsonText
w.close()

