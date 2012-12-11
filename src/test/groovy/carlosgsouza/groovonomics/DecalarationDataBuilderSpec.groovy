package carlosgsouza.groovonomics

import spock.lang.Specification

class DecalarationDataBuilderSpec extends Specification {

	def "DeclarationData objects should be built using a sexy syntax"() {
		given:
		def builder = new ObjectGraphBuilder()
		
		when:
		def data = builder {
			fields {
				s 12
				d 13
			}
			methods {
				s 1
				d 2
			}
		}
		
		then:
		
		data.fields.s == 12
		data.fields.d == 13
		
		and:
		data.methods.s == 1
		data.methods.d == 2
	}
}
