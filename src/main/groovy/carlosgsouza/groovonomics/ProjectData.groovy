package carlosgsouza.groovonomics

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
		result.agregate()
	}
	
	def agregate() {
		agregateClasses { true }
	}
	
	def agregateClasses() {
		agregateClasses { it.isScript == false }
	}
	
	def agregateScripts() {
		agregateClasses { it.isScript }
	}
	
	def agregateTestClasses() {
		agregateClasses { it.isTestClass() }
	}
	
	def agregateFunctionalClasses() {
		agregateClasses { it.isTestClass() == false }
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
