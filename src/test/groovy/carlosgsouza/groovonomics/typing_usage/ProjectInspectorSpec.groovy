package carlosgsouza.groovonomics.typing_usage

import carlosgsouza.groovonomics.typing_usage.ProjectInspector;
import spock.lang.Specification

class ProjectInspectorSpec extends Specification {

	def "getTypeSystemUsageData should return a projectData object with data for all classes"() {
		given:
		def projectsFolder = new File("src/test/resources/carlosgsouza/groovonomics/typing_usage/projects_folder/0001")
		def inspector = new ProjectInspector(projectsFolder)
		
		when:
		def projectData = inspector.getTypeSystemUsageData()
		
		then:
		projectData.name == "a_project"
		projectData.id == "0001"
		projectData.classes.size() == 19
	}
}
