package carlosgsouza.groovonomics

class PrettyPrinter {
	
	def classDataFolder = new File("/Users/carlosgsouza/Dropbox/UFMG/Mestrado/mes/groovonomics/data/classes")
	def agregateDataFolder = new File("/Users/carlosgsouza/Dropbox/UFMG/Mestrado/mes/groovonomics/data/class_agregate")
	
	def classDataFactory = new ClassDataFactory()
	
	def all() {
		def allFile = new File(agregateDataFolder, "all.json")
		def data = classDataFactory.fromJsonFile allFile
		
		printDeclarationCount(data.publicMethodReturn, "Retorno de mŽtodo", "pœblico")
		printDeclarationCount(data.privateMethodReturn, "Retorno de mŽtodo", "privado")
		printDeclarationCount(data.protectedMethodReturn, "Retorno de mŽtodo", "protected")
		printDeclarationCount(data.publicField, "Campo", "pœblico")
		printDeclarationCount(data.privateField, "Campo", "privado")
		printDeclarationCount(data.protectedField, "Campo", "protected")
		printDeclarationCount(data.publicMethodParameter, "Par‰metro de mŽtodo", "pœblico")
		printDeclarationCount(data.privateMethodParameter, "Par‰metro de mŽtodo", "privado")
		printDeclarationCount(data.protectedMethodParameter, "Par‰metro de Mmtodo", "protected")
		printDeclarationCount(data.pureTypeSystemPublicMethods, "MŽtodo com sistema de tipos œnico", "pœblico")
		printDeclarationCount(data.pureTypeSystemPrivateMethods, "MŽtodo com sistema de tipos œnico", "privado")
		printDeclarationCount(data.pureTypeSystemProtectedMethods, "MŽtodo com Sistema de Tipos œnico", "protected")
		printDeclarationCount(data.publicConstructorParameter, "Construtor", "pœblico")
		printDeclarationCount(data.privateConstructorParameter, "Construtor", "privado")
		printDeclarationCount(data.protectedConstructorParameter, "Construtor", "protected")
		printDeclarationCount(data.pureTypeSystemPublicConstructors, "Construtor com sistema de tipos œnico", "pœblico")
		printDeclarationCount(data.pureTypeSystemPrivateConstructors, "Construtor com sistema de tipos œnico", "privado")
		printDeclarationCount(data.pureTypeSystemProtectedConstructors, "Construtor com sistema de tipos œnico", "protected")
		printDeclarationCount(data.localVariable, "Vari‡vel Local ou vari‡vel de closure")
	}
	
	def printDeclarationCount(declarationCount, name) {
		printDeclarationCount(declarationCount, name, "")
	}
	def printDeclarationCount(declarationCount, name, attribute) {
		println "$name\t$attribute\t${declarationCount.s}\t${declarationCount.d}"
	}
	
	public static void main(args) {
		new PrettyPrinter().all()
	}
}
