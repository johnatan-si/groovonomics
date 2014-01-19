package carlosgsouza.groovonomics.data

import groovy.json.JsonSlurper

class ProgrammingLanguages {
	
	File inputFile = new File("../../analysis/data/programmer_background/language_summary.json")
	File outputFile = new File("../../analysis/parsed/languages.txt")
	
	JsonSlurper slurper = new JsonSlurper()
	
	JsonToRTranslator translator = new JsonToRTranslator()
	
	def run() {
		def resultStack = []
		
		if(outputFile.exists()) {
			outputFile.delete()
		}
		
		def language_count = slurper.parseText(inputFile.text).sort{ -it.value }
				
		language_count.eachWithIndex { language, count, i ->
			if(i >= 10) {
				return
			}
			
			
			count.times {
				resultStack.push language
			}
		}
		
		def line
		while(!resultStack.isEmpty()) {
			outputFile << "${resultStack.pop()}\n"
		}
	}
	
	public static void main(args) {
		new ProgrammingLanguages().run()
	}

}
