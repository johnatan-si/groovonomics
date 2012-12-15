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
	
	def toJson() {
		new JsonBuilder(this).toPrettyString()
	}
	
	String toString() {
		toJson()
	}
}
