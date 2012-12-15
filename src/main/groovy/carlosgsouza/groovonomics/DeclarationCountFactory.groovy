package carlosgsouza.groovonomics

class DeclarationCountFactory {
	def fromMap(map) {
		def result = new DeclarationCount()
		result.s = map.s
		result.d = map.d
		result
	}
}
