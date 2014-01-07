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
	File outputFile = new File("../../analysis/result/declaration_by_type.txt")
	
	JsonSlurper slurper = new JsonSlurper()
	
	def DECLARATION_TYPES = [
								"localVariable",
								
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

	def run() {
		def result = []
		
		result << ["project_id", "loc"] + DECLARATION_TYPES
		
		rawDataFolder.eachFile { file ->
			def projectId = file.name - ".json"
			
			result << [projectId, getLOC(projectId)] + getTypeUsage(file)  	
		}
		
		if(outputFile.exists()) {
			outputFile.delete()
		}	
		
		result.each {
			outputFile << it.join("\t") + "\n"
		}
	}
	
	private getLOC(projectId) {
		def metadata = slurper.parseText(new File(metadataFolder, "${projectId}.json").text)
		return metadata.loc
	}

	private getTypeUsage(File file) {
		def data = slurper.parseText(file.text)
		return DECLARATION_TYPES.collect{ (data[it].DRelative + data[it].DRelative) > 0 ? data[it].SRelative : "NA" }
	}
	
	public static void main(args) {
		new DeclarationType().run()
	}

}
