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
		def testResult = [["project_id", "loc", "type", "overall"] + DECLARATION_TYPES_WITHOUT_VISIBILITY + DECLARATION_TYPES_AND_VISIBILITY]
		def scriptResult = [["project_id", "loc", "type", "overall"] + DECLARATION_TYPES_WITHOUT_VISIBILITY + DECLARATION_TYPES_AND_VISIBILITY]
		
		rawDataFolder.eachFile { file ->
			def projectId = file.name - ".json"
			
			def data = slurper.parseText(file.text).classes.collect{ classDataFactory.fromMap(it) }

			processTestData(projectId, data, testResult)
			processScriptData(projectId, data, scriptResult)
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
	
	def processTestData(projectId, data, testResult) {
		def testData = agregateTestData(data)
		def agregateTestData = testData.agregate()
		
		def nonTestData = agregateNonTestData(data)
		def agregateNonTestData = nonTestData.agregate()
		
		if(agregateNonTestData.total > 0 && agregateTestData.total > 0 ) {
			testResult << [projectId, getLOC(projectId), "test",		agregateTestData.SRelative]		+ getTypeUsageWithoutVisibility(testData) + getTypeUsage(testData)
			testResult << [projectId, getLOC(projectId), "non-test",	agregateNonTestData.SRelative]	+ getTypeUsageWithoutVisibility(nonTestData) + getTypeUsage(nonTestData)
		}
	}
	
	def processScriptData(projectId, data, scriptResult) {
		def scriptData = agregateScriptData(data)
		def agregateScriptData = scriptData.agregate()
		
		def nonScriptData = agregateNonScriptData(data)
		def agregateNonScriptData = nonScriptData.agregate()
		
		if(agregateNonScriptData.total > 0 && agregateScriptData.total > 0 ) {
			scriptResult << [projectId, getLOC(projectId), "script",		agregateScriptData.SRelative]		+ getTypeUsageWithoutVisibility(scriptData) + getTypeUsage(scriptData)
			scriptResult << [projectId, getLOC(projectId), "non-script",	agregateNonScriptData.SRelative]	+ getTypeUsageWithoutVisibility(nonScriptData) + getTypeUsage(nonScriptData)
		}
	}
	
	def agregateTestData(data) {
		agregateClassData(data.findAll{it.isTestClass() })
	}
	
	def agregateNonTestData(data) {
		agregateClassData(data.findAll{!it.isTestClass() })
	}
	
	def agregateScriptData(data) {
		agregateClassData(data.findAll{it.isScript })
	}
	
	def agregateNonScriptData(data) {
		agregateClassData(data.findAll{!it.isScript })
	}
	
	def agregateClassData(data) {
		data.inject(new ClassData()) { result, it -> result+it}
	}

	private getTypeUsageWithoutVisibility(data) {
		return DECLARATION_TYPES_WITHOUT_VISIBILITY.collect{ agregateDeclarationsOfType(it, data) }
	}
	
	def agregateDeclarationsOfType(type, data) {
		def declarationsOfType = data.declarationsOfType(type)
		
		def totalS = declarationsOfType.inject(0) { acc, val -> acc + val.s.toDouble() }
		def totalD = declarationsOfType.inject(0) { acc, val -> acc + val.d.toDouble() }
		
		return (totalS + totalD == 0) ? "NA" : totalS/(totalS + totalD)
	}
	
	private getLOC(projectId) {
		def metadata = slurper.parseText(new File(metadataFolder, "${projectId}.json").text)
		return metadata.loc
	}

	private getTypeUsage(data) {
		return DECLARATION_TYPES_AND_VISIBILITY.collect{ (data[it].DRelative + data[it].DRelative) > 0 ? data[it].SRelative : "NA" }
	}
	
	public static void main(args) {
		new ScriptsAndTests().run()
	}

}
