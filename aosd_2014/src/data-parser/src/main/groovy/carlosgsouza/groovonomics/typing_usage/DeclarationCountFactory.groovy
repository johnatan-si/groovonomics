package carlosgsouza.groovonomics.typing_usage

class DeclarationCountFactory {
	def fromMap(map) {
		def result = new DeclarationCount()
		result.s = map?.s ?: 0
		result.d = map?.d ?: 0
		result
	}
}
