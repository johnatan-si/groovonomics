package carlosgsouza.groovonomics.data

import groovy.json.JsonSlurper
import carlosgsouza.groovonomics.typing_usage.ClassData
import carlosgsouza.groovonomics.typing_usage.ClassDataFactory

/*
 * Prepares a table with the relative usage of types per declaration type/visibility for each project
 * 
 * -1 represents a project with any declarations of a given type/visibility
 */
class ScriptsAndTests {
	
	File rawDataFolder = new File("../../analysis/data/type_usage/class/")
	File metadataFolder = new File("../../analysis/data/dataset/metadata")
	
	ClassDataFactory classDataFactory = new ClassDataFactory()
	JsonSlurper slurper = new JsonSlurper()
	
	JsonToRTranslator translator = new JsonToRTranslator()
	
	def run() {
		def testResult = [translator.headers + ["condition"]]
		def scriptResult = [translator.headers + ["condition"]]
		
		def projectFiles = rawDataFolder.listFiles()
		def c = 1
		
		projectFiles.each { file ->
			println "${c++}/${projectFiles.size()}"
			
			def projectId = file.name - ".json"
			
			def data = slurper.parseText(file.text).classes.collect{ classDataFactory.fromMap(it) }

			processProjectData(projectId, data, testResult, "test", {!it.isScript}, {it.isTestClass})
			processProjectData(projectId, data, scriptResult, "script", {true}, {it.isScript})
		}
		
		writeResult("../../analysis/parsed/declaration_by_tests.txt", testResult)
		writeResult("../../analysis/parsed/declaration_by_scripts.txt", scriptResult)
	}
	
	def writeResult(path, result) {
		def file = new File(path)
		
		if(file.exists()) {
			file.delete()
		}
		
		result.each {
			file << it.join("\t") + "\n"
		}
	}
	
	def processProjectData(projectId, dataOfAllClasses, resultList, condition, generalFilter, filter) {
		def dataA = dataOfAllClasses.findAll(generalFilter).findAll(filter)
		def dataB = dataOfAllClasses.findAll(generalFilter).findAll{ !filter(it) }
		
		if(dataA.size() > 0) {
			resultList << translator.translate(projectId, metadataFolder, agregateClassData(dataA)) + [condition]
		} 

		if(dataB.size() > 0) {
			resultList << translator.translate(projectId, metadataFolder, agregateClassData(dataB)) + ["not-$condition"]
		}
	}
	
	def agregateClassData(data) {
		data.inject(new ClassData()) { result, it -> result+it}
	}

	private getLOC(projectId) {
		def metadata = slurper.parseText(new File(metadataFolder, "${projectId}.json").text)
		return metadata.loc
	}

	public static void main(args) {
		new ScriptsAndTests().run()
	}

}
