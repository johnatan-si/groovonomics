package carlosgsouza.groovonomics.typing_usage

import groovy.io.FileType

import java.text.SimpleDateFormat

public class ProjectInspector {
	
	def projectFolder
	def result
	def projectData
	
	def sdf = new SimpleDateFormat()
	
	public ProjectInspector(projectFolder) {
		this.projectFolder = projectFolder
		
		projectData = new ProjectData()
		projectData.id = projectFolder.name
	}
	
	def now() {
		
	}
	
	def getTypeSystemUsageData() {
		projectFolder.eachFileRecurse(FileType.FILES) { 
			if(it.name.endsWith(".groovy")) {
				try {
					println "${now()}	|	Parsing $it.absolutePath"
					def astInspector = new ASTInspector(it.absolutePath)
					projectData.classes.add astInspector.getTypeSystemUsageData()
				} catch(Throwable e) {
					
					println "${now()}	|	WARNING: The following file can't be compiled $it.absolutePath"
					println "--> $e.message"
				}
			}
		}
		
		projectData
	}
}
