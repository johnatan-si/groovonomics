package carlosgsouza.groovonomics

import groovy.io.FileType;

public class ProjectInspector {
	
	def projectFolder
	def result
	
	public ProjectInspector(projectFolder) {
		this.projectFolder = projectFolder
	}
	
	def getTypeSystemUsageData() {
		result = ["fields": ["s":0, "d":0], "methods" : ["s":0, "d":0]]
		
		projectFolder.eachFileRecurse(FileType.FILES) { 
			if(it.name.endsWith(".groovy")) {
				try {
					println "Parsing $it.absolutePath"
					def astInspector = new ASTInspector(it.absolutePath)
					addToResult astInspector.getTypeSystemUsageData()
				} catch(Throwable e) {
					println "WARNING: Impossible to parse $it.absolutePath"
				}
			}
		}
		
		result
	}
	
	def addToResult(parcialResult) {
		result["fields"]["s"] += parcialResult["fields"]["s"]
		result["fields"]["d"] += parcialResult["fields"]["d"]
		
		result["methods"]["s"] += parcialResult["methods"]["s"]
		result["methods"]["d"] += parcialResult["methods"]["d"]
	}
}
