package carlosgsouza.groovonomics.typing_usage

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
	
	DeclarationCount publicConstructorParameter = new DeclarationCount()
	DeclarationCount privateConstructorParameter = new DeclarationCount()
	DeclarationCount protectedConstructorParameter = new DeclarationCount()
	
	DeclarationCount localVariable = new DeclarationCount()
	
	String className = ""
	String location = ""
	Boolean isScript = false
	Boolean importsJunit = false
	Boolean importsSpock = false
	
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
		result.publicConstructorParameter = this.publicConstructorParameter + other.publicConstructorParameter                
		result.privateConstructorParameter = this.privateConstructorParameter + other.privateConstructorParameter               
		result.protectedConstructorParameter = this.protectedConstructorParameter + other.protectedConstructorParameter             
		result.localVariable = this.localVariable + other.localVariable                             
		
		result.className = ""
		result.location = ""
		result.isScript = false
		
		result.importsJunit = importsJunit || other.importsJunit
		result.importsSpock = importsSpock || other.importsSpock 
		
		result
	}
	
	def getDeclarationData(String type) {
		switch(type) {
			case "field":
				return [publicField, privateField, protectedField]
			case "methodParameter":
				return [publicMethodParameter, privateMethodParameter, protectedMethodParameter]
			case "methodReturn":
				return [publicMethodReturn, privateMethodReturn, protectedMethodReturn]
			case "constructorParameter":
				return [publicConstructorParameter, privateConstructorParameter, protectedConstructorParameter]
			case "localVariable":
				return [localVariable] 	
			case "public":
				return [publicField, publicMethodParameter, publicMethodReturn, publicConstructorParameter]
			case "protected":
				return [protectedField, protectedMethodParameter, protectedMethodReturn, protectedConstructorParameter]
			case "private":
				return [privateField, privateMethodParameter, privateMethodReturn, privateConstructorParameter]
			case "all":
				return getDeclarationData("public") + getDeclarationData("private") + getDeclarationData("protected") + localVariable
			default:
				return [getProperty(type)]
		}
	}
	
	def getAll() {
		return getAgregateDeclarationData("all")
	}
	
	def getPublicDeclarations() {
		return getAgregateDeclarationData("public")
	}
	
	def getPrivateDeclarations() {
		return getAgregateDeclarationData("private")
	}
	
	def getProtectedDeclarations() {
		return getAgregateDeclarationData("protected")
	}
	
	def getField() {
		return getAgregateDeclarationData("field")
	}
	
	def getMethodParameter() {
		return getAgregateDeclarationData("methodParameter")
	}
	
	def getMethodReturn() {
		return getAgregateDeclarationData("methodReturn")
	}
	
	def getConstructorParameter() {
		return getAgregateDeclarationData("constructorParameter")
	}
	
	def getAgregateDeclarationData(String type) {
		return getDeclarationData(type).inject(new DeclarationCount()) { result, value -> result + value} 
	}
	
	def getIsTestClass() {
		location.contains("/test/")
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
	
}
