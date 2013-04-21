package carlosgsouza.groovonomics.typing_usage

import groovy.json.JsonBuilder

class DeclarationCount {
	int s = 0
	int d = 0
	
	def plus(dc2) {
		def result = new DeclarationCount()
		
		result.s = s + dc2.s
		result.d = d + dc2.d
		
		result
	}
	
	def getSRelative() {
		if(total == 0) {
			return 0
		}
		
		s / (double)total
	}
	
	def getDRelative() {
		if(total == 0) {
			return 0
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
		//toJson()
		"$s $d  $sRelative $dRelative"
	}
}
