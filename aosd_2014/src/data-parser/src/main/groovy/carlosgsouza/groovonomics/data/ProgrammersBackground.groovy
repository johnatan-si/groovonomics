package carlosgsouza.groovonomics.data

import groovy.json.JsonSlurper
import carlosgsouza.groovonomics.typing_usage.ClassData
import carlosgsouza.groovonomics.typing_usage.ClassDataFactory


class ProgrammersBackground {
	
	File rawDataFolder = new File("../../analysis/data/type_usage/project/")
	File metadataFolder = new File("../../analysis/data/dataset/metadata")
	File programmersFolder = new File("../../analysis/data/programmer_background/users")
	
	def STATICALLY_TYPED_LANGUAGES = ["Haskell", "Go", "C#", "C++", "Objective-C", "C", "Scala", "Java"]
	
	ClassDataFactory classDataFactory = new ClassDataFactory()
	JsonSlurper slurper = new JsonSlurper()
	
	JsonToRTranslator translator = new JsonToRTranslator()
	
	def run() {
		def result = [translator.headers + ["condition"]]
		
		def total = rawDataFolder.listFiles().size()
		def c = 0
		
		
		def programmerBackground = getBackgroundOfAllUsers()
		
		rawDataFolder.eachFile { file ->
			def projectId = file.name - ".json"
			println "${++c}/${total} - ${projectId}"
			
			result << processProjectData(projectId, file, programmerBackground)
		}
		
		writeResult("../../analysis/parsed/declaration_by_background.txt", result)
	}
	
	def getBackgroundOfAllUsers() {
		def result = [:]
		programmersFolder.eachFile { file ->
			result[file.name - ".json"] = getBackground(file.readLines())
		}
		result
	}
	
	def getBackground(languages) {
		languages.unique()
		languages -= ["null", "Groovy"]
		
		def numberOfStaticallyTypedLanguages = languages.intersect(STATICALLY_TYPED_LANGUAGES).size()
		
		if(languages.size() == 0 || numberOfStaticallyTypedLanguages == 0) {
			return "dynamic-only"
		} else if(numberOfStaticallyTypedLanguages == languages.size()) {
			return "static-only"
		} else {
			return "static-and-dynamic"
		}
	}
	
	def writeResult(path, result) {
		def file = new File(path)
		
		if(file.exists()) {
			file.delete()
		}
		
		result.each {
			file << it.join("\t") + "\n"
		}
	}
	
	def processProjectData(projectId, file, programmerBackground) {
		def background = getBackgroundOfProgrammers(projectId, programmerBackground)
		translator.translate(projectId, metadataFolder, file) + [background]
	}
	
	def getBackgroundOfProgrammers(projectId, programmerBackground) {
		def owner = projectId.substring(0, projectId.lastIndexOf("_"))
		return programmerBackground[owner]
	}
	
	def agregateClassData(data) {
		data.inject(new ClassData()) { result, it -> result+it}
	}

	private getLOC(projectId) {
		def metadata = slurper.parseText(new File(metadataFolder, "${projectId}.json").text)
		return metadata.loc
	}

	public static void main(args) {
		new ProgrammersBackground().run()
	}

}
