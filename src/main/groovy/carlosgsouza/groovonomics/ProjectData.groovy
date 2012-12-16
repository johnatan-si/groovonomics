package carlosgsouza.groovonomics

import groovy.json.JsonBuilder

class ProjectData {
	def id = ""
	def name = ""
	def classes = []
	
	def agregate() {
		def result = new ClassData()
		classes.each {
			result += it
		}
		result
	}
	
	def agregateClasses() {
		def result = new ClassData()
		classes.findAll { it.isScript == false }.each {
			result += it
		}
		result.agregate()
	}
	
	def agregateScripts() {
		def result = new ClassData()
		classes.findAll { it.isScript }.each {
			result += it
		}
		result.agregate()
	}
	
	def toJson() {
		new JsonBuilder(this).toPrettyString()
	}
	
	String toString() {
		toJson()
	}
}
