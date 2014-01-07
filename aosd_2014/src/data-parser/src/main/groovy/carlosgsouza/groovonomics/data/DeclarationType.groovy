package carlosgsouza.groovonomics.data

import groovy.json.JsonSlurper;

/*
 * Prepares a table with the relative usage of types per declaration type/visibility for each project
 * 
 * -1 represents a project with any declarations of a given type/visibility
 */
class DeclarationType {
	
	File rawDataFolder = new File("../../analysis/data/type_usage/project/")
	File metadataFolder = new File("../../analysis/data/dataset/metadata")
	File outputFile = new File("../../analysis/parsed/declaration_by_type.txt")
	
	JsonSlurper slurper = new JsonSlurper()
	
	def DECLARATION_TYPES_AND_VISIBILITY = [
								"privateMethodReturn",
								"protectedMethodReturn",
								"publicMethodReturn",
								 
//								"privateField",
//								"protectedField",
//								"publicField",
								
								"privateMethodParameter",
								"protectedMethodParameter",
								"publicMethodParameter"
//								,
//								
//								"privateConstructorParameter",
//								"protectedConstructorParameter",
//								"publicConstructorParameter"
							]
	
	def DECLARATION_TYPES_WITHOUT_VISIBILITY = [
			"localVariable", "methodReturn", "methodParameter"
		]

	def run() {
		def result = []
		
		result << ["project_id", "loc"] + DECLARATION_TYPES_WITHOUT_VISIBILITY + DECLARATION_TYPES_AND_VISIBILITY
		
		rawDataFolder.eachFile { file ->
			def projectId = file.name - ".json"
			
			result << [projectId, getLOC(projectId)] + getTypeUsageWithoutVisibility(file) + getTypeUsage(file)  	
		}
		
		if(outputFile.exists()) {
			outputFile.delete()
		}	
		
		result.each {
			outputFile << it.join("\t") + "\n"
		}
	}

	private getTypeUsageWithoutVisibility(File file) {
		def data = slurper.parseText(file.text)
		
		return DECLARATION_TYPES_WITHOUT_VISIBILITY.collect{ agregateDeclarationsOfType(it, data) }
	}
	
	def agregateDeclarationsOfType(type, data) {
		def declarationsOfType = data.findAll{ it.key.toLowerCase().endsWith(type.toLowerCase()) }*.value
		
		def totalS = declarationsOfType.inject(0) { acc, val -> acc + val.s.toDouble() }
		def totalD = declarationsOfType.inject(0) { acc, val -> acc + val.d.toDouble() }
		
		return (totalS + totalD == 0) ? "NA" : totalS/(totalS + totalD)
	}
	
	private getLOC(projectId) {
		def metadata = slurper.parseText(new File(metadataFolder, "${projectId}.json").text)
		return metadata.loc
	}

	private getTypeUsage(File file) {
		def data = slurper.parseText(file.text)
		return DECLARATION_TYPES_AND_VISIBILITY.collect{ (data[it].DRelative + data[it].DRelative) > 0 ? data[it].SRelative : "NA" }
	}
	
	public static void main(args) {
		new DeclarationType().run()
	}

}
