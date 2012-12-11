package carlosgsouza.groovonomics

class ClassData {
	String name = ""
	String path = ""
	boolean isScript = false
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
}
