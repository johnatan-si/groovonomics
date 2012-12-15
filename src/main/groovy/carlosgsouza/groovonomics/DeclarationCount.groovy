package carlosgsouza.groovonomics

class DeclarationCount {
	int s
	int d
	
	def plus(dc2) {
		def result = new DeclarationCount()
		
		result.s = s + dc2.s
		result.d = d + dc2.d
		
		result
	}
}
