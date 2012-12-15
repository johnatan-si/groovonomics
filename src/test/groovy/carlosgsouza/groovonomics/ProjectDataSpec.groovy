package carlosgsouza.groovonomics

import spock.lang.Specification

class ProjectDataSpec extends Specification {
	def "toJson should return a json string with the project data and the data for each class"() {
		given: "a projectData object with two classes"
		def class1 = new ClassData()
		class1.localVariable.s = 10
		class1.publicConstructorParameter.d = 11
		
		def class2 = new ClassData()
		class2.localVariable.d = 1
		class2.publicConstructorParameter.s = 3
		
		def projectData = new ProjectData(id:"0032", name:"lovely_spock", classes: [class1, class2])
		 
		when:
		def json = projectData.toJson()
			
		then:
		json.startsWith "{\n    \"id\""
	}
	
	def "agregate should return the sum of all classes for each one of the fields"() {
		given: "a projectData object with two classes"
		def class1 = new ClassData()
		class1.localVariable.d = 18
		class1.publicConstructorParameter.d = 11
		
		def class2 = new ClassData()
		class2.localVariable.d = 1
		class2.publicConstructorParameter.s = 3
		
		def projectData = new ProjectData(id:"0032", name:"lovely_spock", classes: [class1, class2])
		 
		when:
		def agregateData = projectData.agregate()
			
		then:
		agregateData.localVariable.d == 19
		agregateData.localVariable.s == 0
		agregateData.publicConstructorParameter.d == 11
		agregateData.publicConstructorParameter.s == 3
	}
}
