import static groovyx.net.http.ContentType.*
import static groovyx.net.http.Method.*
import groovy.json.JsonBuilder
import groovy.json.JsonOutput
import groovyx.net.http.*


class AnalyzeUsersLanguages {
	File baseFolder = new File("/opt/groovonomics/data/")
	
	def staticallyTypedLanguages = ["Haskell", "Go", "C#", "C++", "Objective-C", "C", "Scala", "Java"]
	def dynamicallyTypedLanguages = ["Lua", "Erlang", "ActionScript", "Puppet", "Emacs Lisp", "Perl", "CoffeeScript", "Clojure", "VimL", "CSS", "PHP", "Python", "Shell", "Ruby", "JavaScript"]
	
	def vamoTurrrrrma() {
		
		def language_count = [:]
		def system_count = [staticLanguage:0, dynamicLanguage:0, staticAndDynamicLanguage:0, groovyOnly:0]
		
		new File(baseFolder, "users").eachFile { userFile ->
			def languages = new HashSet<String>(userFile.text.split("\n").collect { it } )
			
			def usesStatic = false
			def usesDynamic = false
			def usesStaticAndDynamic = false
			
			def otherLanguages = languages.findAll{it && it != "Groovy" && it != "null"}
			otherLanguages.each { language ->
				language_count[language] = language_count[language] ? language_count[language]+1 : 1
				
				usesStatic |= staticallyTypedLanguages.contains(language)
				usesDynamic |= dynamicallyTypedLanguages.contains(language)
			}
			
			if(usesStatic && usesDynamic) {
				system_count.staticAndDynamicLanguage++
			}
			else if(usesStatic) {
				system_count.staticLanguage++
			}
			else if(usesDynamic) {
				system_count.dynamicLanguage++
			} else  {
				system_count.groovyOnly++
			}
			
		}
		
		def languageFile = new File(baseFolder, "language_summary.json")
		languageFile.delete()
		languageFile << JsonOutput.prettyPrint(new JsonBuilder(language_count.sort{it.value}).toString())
		
		def systemFile = new File(baseFolder, "system_summary.json")
		systemFile.delete()
		systemFile << JsonOutput.prettyPrint(new JsonBuilder(system_count).toString())
		
	}
	
	public static void main(String[] args) {
		new AnalyzeUsersLanguages().vamoTurrrrrma()
	}
}

