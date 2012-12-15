package carlosgsouza.groovonomics

import spock.lang.Specification


class DeclarationCoundSpec extends Specification {
	def "+ should implement the sum of s and d"() {
		given:
		def dc1 = new DeclarationCount()
		dc1.s = 1
		dc1.d = 2
		
		def dc2 = new DeclarationCount()
		dc2.s = 3
		dc2.d = 4
		
		when:
		def sum = dc1 + dc2
		
		then:
		sum.s == 4 
		sum.d == 6
	}
}
