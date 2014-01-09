package carlosgsouza.groovonomics.data

import groovy.json.JsonSlurper
import carlosgsouza.groovonomics.typing_usage.ClassData
import carlosgsouza.groovonomics.typing_usage.ClassDataFactory
import carlosgsouza.groovonomics.typing_usage.DeclarationCount

public class JsonToRTranslator {
	
	JsonSlurper slurper = new JsonSlurper()
	ClassDataFactory classDataFactory = new ClassDataFactory()

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

	def DECLARATION_TYPE = [
		"localVariable",
		"methodReturn",
		"methodParameter"
//		,
//		field,
//		constructorParameter
	]

	def DECLARATION_VISIBILITY = [
		"public",
		"private",
		"protected"
	]
	
	def METADATA = [
			"loc",
			"commits"
		]

	public getHeaders() {
		["projectId"] + METADATA + classDataHeaders
	}
	
	public translateClassData(File classDataFile) {
		translateClassData(slurper.parseText(classDataFile.text))
	}
	
	public translateClassData(Map classDataMap) {
		translateClassData(classDataFactory.fromMap(classDataMap))
	}
	
	private getClassDataHeaders() {
		(["all"] + DECLARATION_TYPE + DECLARATION_TYPES_AND_VISIBILITY)
	}
	
	public translateMetadata(projectId, metadataFolder) {
		def metadata = slurper.parseText(new File(metadataFolder, "${projectId}.json").text)
		return [projectId, metadata.loc, metadata.commits]
	}
	
	public translateClassData(ClassData classData) {
		classDataHeaders.collect {
			DeclarationCount declarationData = classData.getAgegateDeclarationData(it)
			
//			if(it.contains("eturn")) {
//				return (declarationData.DRelative == 0 ? "NA" : Math.min(1.3 * declarationData.SRelative, 1.0))
//			} else {
				return (declarationData.isEmpty ? "NA" : declarationData.SRelative)
//			}
		}
	}
	
	public translate(projectId, metadataFolder, classData) {
		translateMetadata(projectId, metadataFolder) + translateClassData(classData)
	}

}
