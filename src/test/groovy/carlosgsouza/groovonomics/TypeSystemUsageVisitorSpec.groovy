package carlosgsouza.groovonomics

import org.codehaus.groovy.control.CompilationUnit
import org.codehaus.groovy.control.CompilePhase
import org.codehaus.groovy.control.CompilerConfiguration

import spock.lang.Specification

class TypeSystemUsageVisitorSpec extends Specification {

	def "vai funfar"() {
		given:
		def sourceFile = new File("src/test/resources/carlosgsouza/groovonomics/projects_folder/0001/a_project/ClassWithMethodsWithOnlyOneTypeOfDeclaration.groovy")
		def visitor = new TypeSystemUsageVisitor(sourceFile)
		
		when:
		def data = visitor.getTypeSystemUsageData()
		
		then:
		data != null
	}
	
	
}
