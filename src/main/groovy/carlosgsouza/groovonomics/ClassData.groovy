package carlosgsouza.groovonomics

import groovy.json.JsonBuilder

class ClassData {
	DeclarationCount publicMethodReturn = new DeclarationCount()
	DeclarationCount privateMethodReturn = new DeclarationCount()
	DeclarationCount protectedMethodReturn = new DeclarationCount()
	DeclarationCount publicField = new DeclarationCount()
	DeclarationCount privateField = new DeclarationCount()
	DeclarationCount protectedField = new DeclarationCount()
	DeclarationCount publicMethodParameter = new DeclarationCount() 
	DeclarationCount privateMethodParameter = new DeclarationCount()
	DeclarationCount protectedMethodParameter = new DeclarationCount()
	DeclarationCount pureTypeSystemPublicMethods = new DeclarationCount()
	DeclarationCount pureTypeSystemPrivateMethods = new DeclarationCount()
	DeclarationCount pureTypeSystemProtectedMethods = new DeclarationCount()
	DeclarationCount publicConstructorParameter = new DeclarationCount()
	DeclarationCount privateConstructorParameter = new DeclarationCount()
	DeclarationCount protectedConstructorParameter = new DeclarationCount()
	DeclarationCount pureTypeSystemPublicConstructors = new DeclarationCount()
	DeclarationCount pureTypeSystemPrivateConstructors = new DeclarationCount()
	DeclarationCount pureTypeSystemProtectedConstructors = new DeclarationCount()
	DeclarationCount localVariable = new DeclarationCount()
	
	int numberOfPublicConstructors = 0
	int numberOfPrivateConstructors = 0
	int numberOfProtectedConstructors = 0
	
	String className = ""
	String location = ""
	Boolean isScript = false
	
	def plus(other) {
		def result = new ClassData() 
		                                                                                        
		result.publicMethodReturn = this.publicMethodReturn + other.publicMethodReturn                         
		result.privateMethodReturn = this.privateMethodReturn + other.privateMethodReturn                   
		result.protectedMethodReturn = this.protectedMethodReturn + other.protectedMethodReturn                     
		result.publicField = this.publicField + other.publicField                               
		result.privateField = this.privateField + other.privateField                              
		result.protectedField = this.protectedField + other.protectedField                            
		result.publicMethodParameter = this.publicMethodParameter + other.publicMethodParameter                     
		result.privateMethodParameter = this.privateMethodParameter + other.privateMethodParameter                    
		result.protectedMethodParameter = this.protectedMethodParameter + other.protectedMethodParameter                  
		result.pureTypeSystemPublicMethods = this.pureTypeSystemPublicMethods + other.pureTypeSystemPublicMethods               
		result.pureTypeSystemPrivateMethods = this.pureTypeSystemPrivateMethods + other.pureTypeSystemPrivateMethods              
		result.pureTypeSystemProtectedMethods = this.pureTypeSystemProtectedMethods + other.pureTypeSystemProtectedMethods            
		result.publicConstructorParameter = this.publicConstructorParameter + other.publicConstructorParameter                
		result.privateConstructorParameter = this.privateConstructorParameter + other.privateConstructorParameter               
		result.protectedConstructorParameter = this.protectedConstructorParameter + other.protectedConstructorParameter             
		result.pureTypeSystemPublicConstructors = this.pureTypeSystemPublicConstructors + other.pureTypeSystemPublicConstructors          
		result.pureTypeSystemPrivateConstructors = this.pureTypeSystemPrivateConstructors + other.pureTypeSystemPrivateConstructors         
		result.pureTypeSystemProtectedConstructors = this.pureTypeSystemProtectedConstructors + other.pureTypeSystemProtectedConstructors       
		result.localVariable = this.localVariable + other.localVariable                             
		
		result.numberOfPublicConstructors = this.numberOfPublicConstructors + other.numberOfPublicConstructors 
		result.numberOfPrivateConstructors = this.numberOfPrivateConstructors + other.numberOfPrivateConstructors
		result.numberOfProtectedConstructors = this.numberOfProtectedConstructors + other.numberOfProtectedConstructors
		
		String className = ""
		String location = ""
		Boolean isScript = false
		
		result
	}
	
	
	
	def agregate() {
		def result = new DeclarationCount()
			
		result += publicMethodReturn
		result += privateMethodReturn
		result += protectedMethodReturn
		result += publicField
		result += privateField
		result += protectedField
		result += publicMethodParameter
		result += privateMethodParameter
		result += protectedMethodParameter
		result += publicConstructorParameter
		result += privateConstructorParameter
		result += protectedConstructorParameter
		result += localVariable
		
		result
	}
	
	@Override
	String toString() {
		new JsonBuilder(this).toPrettyString()
	}
	
//	ClassData(classDataJson) {
//		className = classDataJson.className
//		location = classDataJson.location
//		isScript = classDataJson.isScript
//	}
}
