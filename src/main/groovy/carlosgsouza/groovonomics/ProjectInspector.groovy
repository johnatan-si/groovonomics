package carlosgsouza.groovonomics

import groovy.io.FileType;

public class ProjectInspector {
	
	def projectFolder
	def result
	def projectData
	
	public ProjectInspector(projectFolder) {
		this.projectFolder = projectFolder
		
		projectData = new ProjectData()
		projectData.id = projectFolder.name
		projectData.name = projectFolder.listFiles().find{ !it.name.startsWith(".") }.name
	}
	
	def getTypeSystemUsageData() {
		projectFolder.eachFileRecurse(FileType.FILES) { 
			if(it.name.endsWith(".groovy")) {
				try {
					println "Parsing $it.absolutePath"
					def astInspector = new ASTInspector(it.absolutePath)
					projectData.classes.add astInspector.getTypeSystemUsageData()
				} catch(Throwable e) {
					println "WARNING: The following file can't be compiled $it.absolutePath"
				}
			}
		}
		
		projectData
	}
}
