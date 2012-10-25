import spock.lang.Specification
import carlosgsouza.groovonomics.*

class ProjectInspectorSpec extends Specification {

	def "parsing all classes of a project"() {
		given:
		def projectsFolder = new File("src/test/resources/carlosgsouza/groovonomics/projects_folder/0001")
		def inspector = new ProjectInspector(projectsFolder)
		
		when:
		def data = inspector.getTypeSystemUsageData()
		
		then:
		data["fields"]["s"] == 3
		data["fields"]["d"] == 6

		and:
		data["methods"]["s"] == 9
		data["methods"]["d"] == 6
	}
}
