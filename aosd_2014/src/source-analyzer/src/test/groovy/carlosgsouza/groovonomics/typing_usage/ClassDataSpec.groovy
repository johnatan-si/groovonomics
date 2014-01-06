package carlosgsouza.groovonomics.typing_usage

import carlosgsouza.groovonomics.typing_usage.ClassData;
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

		and: "number of constructors are equal to 0"
		data.numberOfPublicConstructors == 0
		data.numberOfPrivateConstructors == 0
		data.numberOfProtectedConstructors == 0
		
		and: "metadata is blank"
		data.className == ""
		data.location == ""
	}
	
	def "+ should return a classData with the sum of the counters of the two classData objects leaving other fields blank"() {
		given: 
		def data1 = new ClassData()
		data1.publicMethodReturn.s = 0
		data1.publicMethodReturn.d = 2
		data1.protectedMethodReturn.d = 4
		data1.protectedMethodReturn.s = 6
		
		and:
		def data2 = new ClassData()
		data2.publicMethodReturn.s = 1
		data2.publicMethodReturn.d = 3
		data2.protectedMethodReturn.d = 5
		data2.protectedMethodReturn.s = 7
		
		when:
		def sum = data1 + data2
		
		then:
		sum.publicMethodReturn.s == 1
		sum.publicMethodReturn.d == 5
		sum.protectedMethodReturn.d == 9
		sum.protectedMethodReturn.s == 13
		
		and:
		sum.publicConstructorParameter.s == 0
		sum.publicConstructorParameter.d == 0
		sum.protectedConstructorParameter.s == 0
		sum.protectedConstructorParameter.s == 0
		sum.privateConstructorParameter.s == 0
		sum.privateConstructorParameter.d == 0
		
		and: 
		sum.className == ""
		sum.location == ""
		sum.isScript == false
	}
}
