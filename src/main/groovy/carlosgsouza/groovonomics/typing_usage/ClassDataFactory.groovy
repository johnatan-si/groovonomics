package carlosgsouza.groovonomics.typing_usage

import groovy.json.JsonSlurper

class ClassDataFactory {
	
	def declarationCountFactory = new DeclarationCountFactory()
	
	def fromJsonFile(jsonFile) {
		def json = new JsonSlurper().parseText(jsonFile.text)
		fromMap json
	}
	
	def fromMap(map) {
		def result = new ClassData()
		
		result.publicMethodReturn = declarationCountFactory.fromMap map.publicMethodReturn
		result.privateMethodReturn = declarationCountFactory.fromMap map.privateMethodReturn
		result.protectedMethodReturn = declarationCountFactory.fromMap map.protectedMethodReturn
		result.publicField = declarationCountFactory.fromMap map.publicField
		result.privateField = declarationCountFactory.fromMap map.privateField
		result.protectedField = declarationCountFactory.fromMap map.protectedField
		result.publicMethodParameter = declarationCountFactory.fromMap map.publicMethodParameter
		result.privateMethodParameter = declarationCountFactory.fromMap map.privateMethodParameter
		result.protectedMethodParameter = declarationCountFactory.fromMap map.protectedMethodParameter
		result.pureTypeSystemPublicMethods = declarationCountFactory.fromMap map.pureTypeSystemPublicMethods
		result.pureTypeSystemPrivateMethods = declarationCountFactory.fromMap map.pureTypeSystemPrivateMethods
		result.pureTypeSystemProtectedMethods = declarationCountFactory.fromMap map.pureTypeSystemProtectedMethods
		result.publicConstructorParameter = declarationCountFactory.fromMap map.publicConstructorParameter
		result.privateConstructorParameter = declarationCountFactory.fromMap map.privateConstructorParameter
		result.protectedConstructorParameter = declarationCountFactory.fromMap map.protectedConstructorParameter
		result.pureTypeSystemPublicConstructors = declarationCountFactory.fromMap map.pureTypeSystemPublicConstructors
		result.pureTypeSystemPrivateConstructors = declarationCountFactory.fromMap map.pureTypeSystemPrivateConstructors
		result.pureTypeSystemProtectedConstructors = declarationCountFactory.fromMap map.pureTypeSystemProtectedConstructors
		result.localVariable = declarationCountFactory.fromMap map.localVariable
		
		result.numberOfPublicConstructors = map.numberOfPublicConstructors 
		result.numberOfPrivateConstructors = map.numberOfPrivateConstructors
		result.numberOfProtectedConstructors = map.numberOfProtectedConstructors
		
		result.className = map.className
		result.location = map.location
		result.isScript = map.isScript
		
		result
	}
}
