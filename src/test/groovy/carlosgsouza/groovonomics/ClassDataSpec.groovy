package carlosgsouza.groovonomics

import spock.lang.Specification

class ClassDataSpec extends Specification {

	def "a just created ClassData instance should have all declarataions counters set to 0"() {
		when: "a ClassData instance is create"
		def data = new ClassData()
		
		then: "method return declaration counters are equal to 0"
		data.publicMethodReturn.s == 0
		data.publicMethodReturn.d == 0
		data.protectedMethodReturn.d == 0
		data.protectedMethodReturn.s == 0
		data.privateMethodReturn.s == 0
		data.privateMethodReturn.d == 0
		
		and: "method parameters declaration counters are equal to 0"
		data.publicMethodParameter.s == 0
		data.publicMethodParameter.d == 0
		data.protectedMethodParameter.s == 0
		data.protectedMethodParameter.s == 0
		data.privateMethodParameter.s == 0
		data.privateMethodParameter.d == 0
		
		and: "constructor parameter declaration counters are equal to 0"
		data.publicConstructorParameter.s == 0
		data.publicConstructorParameter.d == 0
		data.protectedConstructorParameter.s == 0
		data.protectedConstructorParameter.s == 0
		data.privateConstructorParameter.s == 0
		data.privateConstructorParameter.d == 0
		
		and: "fields declaration counters are equal to 0"
		data.publicField.s == 0
		data.publicField.d == 0
		data.privateField.s == 0
		data.privateField.d == 0
		data.protectedField.s == 0
		data.protectedField.d == 0
		
		and: "local declaration counters are equal to 0"
		data.localVariable.s == 0
		data.localVariable.d == 0
		
		and: "counters for pure type system methods that have at least one paramerter are equal to 0"
		data.pureTypeSystemPublicMethods.s == 0
		data.pureTypeSystemPublicMethods.d == 0
		data.pureTypeSystemPrivateMethods.s == 0
		data.pureTypeSystemPrivateMethods.d == 0
		data.pureTypeSystemProtectedMethods.s == 0
		data.pureTypeSystemProtectedMethods.d == 0
		
		and: "pure type system constructor counters are equal to 0"
		data.pureTypeSystemPublicConstructors.s == 0
		data.pureTypeSystemPublicConstructors.d == 0
		data.pureTypeSystemPrivateConstructors.s == 0
		data.pureTypeSystemPrivateConstructors.d == 0
		data.pureTypeSystemProtectedConstructors.s == 0
		data.pureTypeSystemProtectedConstructors.d == 0

		and: "other counters are equal to 0"
		data.numberOfPublicConstructors == 0
		data.numberOfPrivateConstructors == 0
		data.numberOfProtectedConstructors == 0
		
	}
}
