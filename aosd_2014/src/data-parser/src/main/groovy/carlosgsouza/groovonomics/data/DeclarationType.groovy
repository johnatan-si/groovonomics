package carlosgsouza.groovonomics.data

import groovy.json.JsonSlurper

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
	
	JsonToRTranslator translator = new JsonToRTranslator()
	
	def run() {
		def result = []
		
		result << translator.headers
		
		def projectFiles = rawDataFolder.listFiles()
		def c = 1
		
		projectFiles.each { file ->
			println "${c++}/${projectFiles.size}"
			
			def projectId = file.name - ".json"
			result << translator.translate(projectId, metadataFolder, file)
		}
		
		if(outputFile.exists()) {
			outputFile.delete()
		}	
		
		result.each {
			outputFile << it.join("\t") + "\n"
		}
	}
	
	private getProjectMetadata(projectId) {
		def metadata = slurper.parseText(new File(metadataFolder, "${projectId}.json").text)
		return [metadata.loc, metadata.commits]
	}
	
	public static void main(args) {
		new DeclarationType().run()
	}

}
