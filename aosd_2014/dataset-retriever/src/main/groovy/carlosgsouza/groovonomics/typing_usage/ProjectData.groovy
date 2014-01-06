package carlosgsouza.groovonomics.typing_usage

import groovy.json.JsonBuilder

class ProjectData {
	def id = ""
	def name = ""
	def classes = []
	
	def agregateClasses(classFilter) {
		def result = new ClassData()
		classes.findAll(classFilter).each {
			result += it
		}
		result
	}
	
	def agregate() {
		agregateClasses { true }
	}
	
	def agregateClasses() {
		agregateClasses { it.isScript == false }.agregate()
	}
	
	def agregateScripts() {
		agregateClasses { it.isScript }.agregate()
	}
	
	def agregateTestClasses() {
		agregateClasses { it.isTestClass() }.agregate()
	}
	
	def agregateFunctionalClasses() {
		agregateClasses { it.isTestClass() == false }.agregate()
	}
	
	def hasTests() {
		classes.any { it.isTestClass() } 
	}
	
	def toJson() {
		new JsonBuilder(this).toPrettyString()
	}
	
	String toString() {
		toJson()
	}
}
