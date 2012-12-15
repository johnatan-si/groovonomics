package carlosgsouza.groovonomics;

import groovy.json.JsonSlurper

public class ProjectDataFactory {
	def classDataFactory = new ClassDataFactory()
	def fromJsonFile(jsonFile) {
		def result = new ProjectData()
		
		def json = new JsonSlurper().parseText(jsonFile.text)
		
		result.id = json.id
		result.name = json.name
		
		json.classes.each {
			result.classes.add classDataFactory.fromMap(it)
		}
		
		result
	}
	
}
