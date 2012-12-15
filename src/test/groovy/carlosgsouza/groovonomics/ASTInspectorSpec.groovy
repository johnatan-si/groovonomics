package carlosgsouza.groovonomics

import spock.lang.Specification

class ASTInspectorSpec extends Specification {
	
	def "getting field declaration data"() {
		given: "a class with fields"
		def sourceFile = "src/test/resources/carlosgsouza/groovonomics/projects_folder/0001/a_project/ClassWithFieldsOnly.groovy"
		
		when: "you ask for the type system usage data"
		def data = new ASTInspector(sourceFile).getTypeSystemUsageData()
		
		then: "you get the declaration counts for fields grouped by access modifiers"
		data.publicField.s == 4
		data.publicField.d == 1
		data.privateField.s == 5
		data.privateField.d == 2
		data.protectedField.s == 6
		data.protectedField.d == 3
		
	}
	
	def "properties are considered private fields"() {
		given: "a class with properties"
		def sourceFile = "src/test/resources/carlosgsouza/groovonomics/projects_folder/0001/a_project/ClassWithPropertiesOnly.groovy"
		
		when: "you ask for the type system usage data"
		def data = new ASTInspector(sourceFile).getTypeSystemUsageData()
		
		then: "you get the declaration counts for properties as if they were private fields grouped by access modifiers"
		data.privateField.s == 2
		data.privateField.d == 1
		data.publicField.s == 0
		data.publicField.d == 0
		data.protectedField.s == 0
		data.protectedField.d == 0
	}
	
	def "getting method return declaration data"() {
		given: "a class with methods"
		def sourceFile = "src/test/resources/carlosgsouza/groovonomics/projects_folder/0001/a_project/ClassWithMethodsOnly.groovy"
		
		when: "you ask for the type system usage data"
		def data = new ASTInspector(sourceFile).getTypeSystemUsageData()
		
		then: "you get the declaration counts for fields grouped by access modifiers"
		data.protectedMethodReturn.d == 1
		data.protectedMethodReturn.s == 2
		data.publicMethodReturn.d == 3
		data.publicMethodReturn.s == 4
		data.privateMethodReturn.d == 5
		data.privateMethodReturn.s == 6
		
	}
	
	def "static method data is also retrieved"() {
		given: "a class with STATIC methods"
		def sourceFile = "src/test/resources/carlosgsouza/groovonomics/projects_folder/0001/a_project/ClassWithStaticMethodsOnly.groovy"
		
		when: "you ask for the type system usage data"
		def data = new ASTInspector(sourceFile).getTypeSystemUsageData()
		
		then: "you get the declaration counts for fields grouped by access modifiers"
		data.protectedMethodReturn.d == 1
		data.protectedMethodReturn.s == 2
		data.publicMethodReturn.d == 3
		data.publicMethodReturn.s == 4
		data.privateMethodReturn.d == 5
		data.privateMethodReturn.s == 6
		
	}
	
	def "getting data for method parameters"() {
		given: "a class with methods with parameters"
		def sourceFile = "src/test/resources/carlosgsouza/groovonomics/projects_folder/0001/a_project/ClassWithMethodsWithParameters.groovy"
		
		when: "you ask for the type system usage data"
		def data = new ASTInspector(sourceFile).getTypeSystemUsageData()
		
		then: "you get the declaration counts for parameters grouped by access modifiers of the methods (return type doesn't matter)"
		data.publicMethodParameter.s == 1
		data.publicMethodParameter.d == 2
		data.privateMethodParameter.s == 3
		data.privateMethodParameter.d == 4
		data.protectedMethodParameter.s == 5
		data.protectedMethodParameter.d == 6
	}
	
	def "void methods are considered statically typed"() {
		given: "a class with void methods"
		def sourceFile = "src/test/resources/carlosgsouza/groovonomics/projects_folder/0001/a_project/ClassWithVoidMethods.groovy"
		
		when: "you ask for the type system usage data"
		def data = new ASTInspector(sourceFile).getTypeSystemUsageData()
		
		then: "the void methods are considered statically typed"
		data.protectedMethodReturn.d == 0
		data.protectedMethodReturn.s == 1
		data.publicMethodReturn.d == 0
		data.publicMethodReturn.s == 1
		data.privateMethodReturn.d == 0
		data.privateMethodReturn.s == 1
	}
	
	def "getting data for constructor"() {
		given: "a class with constructors"
		def sourceFile = "src/test/resources/carlosgsouza/groovonomics/projects_folder/0001/a_project/ClassWithConstructors.groovy"
		
		when: "you ask for the type system usage data"
		def data = new ASTInspector(sourceFile).getTypeSystemUsageData()
		
		then: "constructors are not considered methods"
		data.protectedMethodReturn.d == 0
		data.protectedMethodReturn.s == 0
		data.publicMethodReturn.d == 0
		data.publicMethodReturn.s == 0
		data.privateMethodReturn.d == 0
		data.privateMethodReturn.s == 0
		data.publicMethodParameter.s == 0
		data.publicMethodParameter.d == 0
		data.protectedMethodParameter.s == 0
		data.protectedMethodParameter.d == 0
		data.privateMethodParameter.s == 0
		data.privateMethodParameter.d == 0
		
		and: "the number of constructors is recorded"
		data.numberOfPublicConstructors == 5
		data.numberOfPrivateConstructors == 4
		data.numberOfProtectedConstructors == 4
		
		and: "constructor parameter data is recorded"
		data.publicConstructorParameter.s == 4
		data.publicConstructorParameter.d == 5
		data.privateConstructorParameter.s == 4
		data.privateConstructorParameter.d == 11
		data.protectedConstructorParameter.s == 4
		data.protectedConstructorParameter.d == 20
		
		and: "pure type constructors with at least one parameter are considered"
		data.pureTypeSystemPublicConstructors.s == 1
		data.pureTypeSystemPublicConstructors.d == 2
		data.pureTypeSystemPrivateConstructors.s == 1
		data.pureTypeSystemPrivateConstructors.d == 2
		data.pureTypeSystemProtectedConstructors.s == 1
		data.pureTypeSystemProtectedConstructors.d == 2
	}
	
	def "counting methods that use only static or only dynamic declarations"() {
		given: "a class with methods that have only static or only dynamic declarations"
		def sourceFile = "src/test/resources/carlosgsouza/groovonomics/projects_folder/0001/a_project/ClassWithMethodsWithOnlyOneTypeOfDeclaration.groovy"
		
		when: "you ask for the type system usage data"
		def data = new ASTInspector(sourceFile).getTypeSystemUsageData()
		
		then: "you get the count of mthods with no parameters that, hence, use only one type system (for return type)"
		data.pureTypeSystemPublicMethods.s == 1
		data.pureTypeSystemPublicMethods.d == 1
		data.pureTypeSystemPrivateMethods.s == 1
		data.pureTypeSystemPrivateMethods.d == 1
		data.pureTypeSystemProtectedMethods.s == 1
		data.pureTypeSystemProtectedMethods.d == 1
		
	}
	
	def "counting local variable declarations of methods"() {
		given: "a class with methods that have local variables"
		def sourceFile = "src/test/resources/carlosgsouza/groovonomics/projects_folder/0001/a_project/ClassWithMethodsWithLocalVariables.groovy"
		
		when: "you ask for the type system usage data"
		def data = new ASTInspector(sourceFile).getTypeSystemUsageData()
		
		then: "you get the count of local variable declarations"
		data.localVariable.s == 1
		data.localVariable.d == 2
	}
	
	def "local variables inside blocks are also counted"() {
		given: "a class with methods that have local variables declared inside blocks such as if, while and do-while"
		def sourceFile = "src/test/resources/carlosgsouza/groovonomics/projects_folder/0001/a_project/ClassWithMethodsWithLocalVariablesInsideBlocks.groovy"
		
		when: "you ask for the type system usage data"
		def data = new ASTInspector(sourceFile).getTypeSystemUsageData()
		
		then: "you get the count of local variable declarations"
		data.localVariable.s == 1
		data.localVariable.d == 2
	}
	
	def "variables declared inside a for loop are also counted"() {
		given: "a class with a variable declared inside a for loop"
		def sourceFile = "src/test/resources/carlosgsouza/groovonomics/projects_folder/0001/a_project/ClassWithMethodsWithLocalVariablesInsideForLoop.groovy"
		
		when: "you ask for the type system usage data"
		def data = new ASTInspector(sourceFile).getTypeSystemUsageData()
		
		then: "you get the count of local variable declarations"
		data.localVariable.s == 1
		data.localVariable.d == 2
	}
	
	def "variables declared inside closures are also counted"() {
		given: "a class with a variable declared inside a for loop"
		def sourceFile = "src/test/resources/carlosgsouza/groovonomics/projects_folder/0001/a_project/ClassWithMethodsWithLocalVariablesInsideClosures.groovy"
		
		when: "you ask for the type system usage data"
		def data = new ASTInspector(sourceFile).getTypeSystemUsageData()
		
		then: "you get the count of local variable declarations"
		data.localVariable.s == 1
		data.localVariable.d == 2
	}
	
	def "closure parameters are NOT considered local variables"() {
		given: "a class with a variable declared inside a for loop"
		def sourceFile = "src/test/resources/carlosgsouza/groovonomics/projects_folder/0001/a_project/ClassWithMethodsWithClosureParameters.groovy"
		
		when: "you ask for the type system usage data"
		def data = new ASTInspector(sourceFile).getTypeSystemUsageData()
		
		then: "closure parameters are NOT considered local variables"
		data.localVariable.s == 0
		data.localVariable.d == 0
	}
	
	def "closure, local, fields and parameter declarations don't interefere with each other"() {
		given: "a class with closures, parameters, fields and local variables"
		def sourceFile = "src/test/resources/carlosgsouza/groovonomics/projects_folder/0001/a_project/ClassWithClosuresParametersFieldsAndLocals.groovy"
		
		when: "you ask for the type system usage data"
		def data = new ASTInspector(sourceFile).getTypeSystemUsageData()
		
		then: 
		data.privateField.s == 1
		data.privateField.d == 1
		data.publicField.d == 1
		data.publicField.s == 0
		data.protectedField.s == 0
		data.protectedField.d == 0
		
		and:
		data.publicMethodReturn.s == 1
		data.publicMethodReturn.d == 0
		data.protectedMethodReturn.d == 0
		data.protectedMethodReturn.s == 0
		data.privateMethodReturn.d == 0
		data.privateMethodReturn.s == 0
		
		and:
		data.localVariable.s == 9
		data.localVariable.d == 2
	}
	
	def "class metadata is stored"() {
		given: "a class"
		def sourceFile = "src/test/resources/carlosgsouza/groovonomics/projects_folder/0001/a_project/a/packagename/with/multiple/levels/Class.groovy"
		
		when: "you ask for the type system usage data"
		def data = new ASTInspector(sourceFile).getTypeSystemUsageData()
		
		then: "the path to the file"
		data.location == sourceFile
		
		and: "you get the fully qualified name of the class"
		data.className == "a.packagename.with.multiple.levels.Class"
	}
	
	def "a class is not a script"() {
		given: "a class"
		def sourceFile = "src/test/resources/carlosgsouza/groovonomics/projects_folder/0001/a_project/a/packagename/with/multiple/levels/Class.groovy"
		
		when: "you ask for the type system usage data"
		def data = new ASTInspector(sourceFile).getTypeSystemUsageData()
		
		then: "it is not a script"
		data.isScript == false
	}
	
	def "should be able to get data for a script"() {
		given: "a class"
		def sourceFile = "src/test/resources/carlosgsouza/groovonomics/projects_folder/0001/a_project/Script.groovy"
		
		when: "you ask for the type system usage data"
		def data = new ASTInspector(sourceFile).getTypeSystemUsageData()
		
		then: "it is a script"
		data.isScript == true
		data.localVariable.s == 1
		data.localVariable.d == 2
	}
}
