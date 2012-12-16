package carlosgsouza.groovonomics

import groovy.json.JsonBuilder

class DeclarationCount {
	int s
	int d
	
	def plus(dc2) {
		def result = new DeclarationCount()
		
		result.s = s + dc2.s
		result.d = d + dc2.d
		
		result
	}
	
	def getSRelative() {
		if(total == 0) {
			0
		}
		
		s / (double)total
	}
	
	def getDRelative() {
		if(total == 0) {
			0
		}
		
		d / (double)total
	}
	
	def getTotal() {
		s + d
	}
	
	def toJson() {
		new JsonBuilder(this).toPrettyString()
	}
	
	String toString() {
		toJson()
	}
}
