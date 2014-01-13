package carlosgsouza.groovonomics.data

import groovy.json.JsonSlurper

import java.text.SimpleDateFormat

import carlosgsouza.groovonomics.typing_usage.ClassData
import carlosgsouza.groovonomics.typing_usage.ClassDataFactory
import carlosgsouza.groovonomics.typing_usage.DeclarationCount

public class JsonToRTranslator {
	
	JsonSlurper slurper = new JsonSlurper()
	ClassDataFactory classDataFactory = new ClassDataFactory()
	
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd")

	def DECLARATION_TYPES_AND_VISIBILITY = [
		"privateMethodReturn",
		"protectedMethodReturn",
		"publicMethodReturn",

		"privateMethodParameter",
		"protectedMethodParameter",
		"publicMethodParameter",
		
		"privateConstructorParameter",
		"protectedConstructorParameter",
		"publicConstructorParameter",
		
		"privateField",
		"protectedField",
		"publicField"
	]

	def DECLARATION_TYPE = [
		"localVariable",
		"methodReturn",
		"methodParameter",
		"constructorParameter",
		"field"
	]

	def DECLARATION_VISIBILITY = [
		"private",
		"protected",
		"public"
	]
	
	def METADATA = [
			"loc",
			"commits",
			"age"
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
		(["all"] + DECLARATION_TYPE + DECLARATION_TYPES_AND_VISIBILITY + DECLARATION_VISIBILITY)
	}
	
	public translateMetadata(projectId, metadataFolder) {
		def metadata = slurper.parseText(new File(metadataFolder, "${projectId}.json").text)
		return [projectId, metadata.loc, metadata.commits, calculateAge(metadata)]
	}
	
	def calculateAge(metadata) {
		def createDay = sdf.parse(metadata.createdAt)
		def updateDay = sdf.parse(metadata.updatedAt)
		
		return (updateDay - createDay)
	}
	
	public translateClassData(ClassData classData) {
		classDataHeaders.collect {
			DeclarationCount declarationData = classData.getAgregateDeclarationData(it)
			return (declarationData.isEmpty ? "NA" : declarationData.SRelative)
		}
	}
	
	public translate(projectId, metadataFolder, classData) {
		translateMetadata(projectId, metadataFolder) + translateClassData(classData)
	}

}
