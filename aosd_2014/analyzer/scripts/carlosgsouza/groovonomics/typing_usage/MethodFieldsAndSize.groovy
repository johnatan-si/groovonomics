import groovy.json.JsonSlurper


def sizeFile = new File("/Users/carlosgsouza/Dropbox/UFMG/Mestrado/mes/groovonomics/data/size/size.json")
def sizeJson = new JsonSlurper().parseText(sizeFile.text)

def dataFolder = new File("/Users/carlosgsouza/Dropbox/UFMG/Mestrado/mes/groovonomics/data/usage/")

def result = [:]
def resultsCount = [:]

dataFolder.eachFile { projectDataFile ->
	def id = projectDataFile.name.substring(0, 4)
	def projectData = new JsonSlurper().parseText(projectDataFile.text)
	
	def bucket = 0
	def maxSize = 200
	
	def projectSize = sizeJson[id].lines

	while(projectSize > maxSize) {
		bucket++
		maxSize *= 2
	}
	
	if(!resultsCount[bucket]) {
		resultsCount[bucket] = 0
		result[bucket] = 0
	}
	
	def total = projectData["fields"]["s"] + projectData["fields"]["d"]
	
	if(total > 0) {
		result[bucket]+= projectData["fields"]["s"]/total
		resultsCount[bucket]++
	}
	 
//	println id
}

def size = 200
result.size().times {
	println "$size" + "\t" + result[it]/resultsCount[it]
	size *=2
}
//def outputFolder = new File(dataFolder, "size")
//def jsonText = JsonOutput.prettyPrint(new JsonBuilder(result).toString())
//def allResultFile = new File(outputFolder, "size.json")
//allResultFile << jsonText
