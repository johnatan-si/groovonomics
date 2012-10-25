import spock.lang.Specification
import carlosgsouza.groovonomics.*

class ASTInspectorSpec extends Specification {

	def "parsing a simple class"() {
		given:
		def inspector = new ASTInspector("src/test/resources/carlosgsouza/groovonomics/projects_folder/0001/a_project/AClass.groovy")
		
		when:
		def data = inspector.getTypeSystemUsageData()
		
		then:
		data["fields"]["s"] == 1
		data["fields"]["d"] == 2

		and:
		data["methods"]["s"] == 3
		data["methods"]["d"] == 2
	}
}
