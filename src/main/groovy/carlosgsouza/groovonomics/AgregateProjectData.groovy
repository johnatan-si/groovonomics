package carlosgsouza.groovonomics

import groovy.json.JsonBuilder

class AgregateProjectData {
	def id = 0
	def data = new ClassData()
	
	def toJson() {
		new JsonBuilder(this).toPrettyString()
	}
	
	String toString() {
		toJson()
	}
}
