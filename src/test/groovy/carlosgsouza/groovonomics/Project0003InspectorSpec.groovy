package carlosgsouza.groovonomics

import spock.lang.Specification

class Project0003InspectorSpec extends Specification {

	def "parsing all classes of a project"() {
		given:
		def projectsFolder = new File("/Users/carlosgsouza/selected_projects/0120")
		def inspector = new ProjectInspector(projectsFolder)
		
		when:
		def data = inspector.getTypeSystemUsageData()
		
		then:
		data["fields"]["s"] != null
		data["fields"]["d"] != null

		and:
		data["methods"]["s"] != null
		data["methods"]["d"] != null
	}
}
