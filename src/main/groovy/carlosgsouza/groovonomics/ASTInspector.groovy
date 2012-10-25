package carlosgsouza.groovonomics

import groovy.json.JsonBuilder
import groovy.json.JsonOutput

import org.codehaus.groovy.ast.ASTNode
import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.builder.AstBuilder
import org.codehaus.groovy.control.CompilePhase

class ASTInspector {

	private sourceFile

	public ASTInspector(sourceFilePath) {
		sourceFile = new File(sourceFilePath)
	}

	def getFieldsTypeSystemUsageData(classNode) {
		def result = ["s":0, "d":0]

		classNode.fields.each {
			if(it.isDynamicTyped()) {
				result["d"]++
			} else {
				result["s"]++
			}
		}

		result
	}

	def getMethodsTypeSystemUsageData(classNode) {
		def result = ["s":0, "d":0]

		classNode.methods.each {
			if(it.isDynamicReturnType()) {
				result["d"]++
			} else {
				result["s"]++
			}
		}

		result
	}

	def getTypeSystemUsageData() {
		List<ASTNode> nodes = new AstBuilder().buildFromString(CompilePhase.CONVERSION, false, sourceFile.text)
		def classNode = nodes.find { it.class == ClassNode.class }
		
		def fields = getFieldsTypeSystemUsageData(classNode)
		def methods = getMethodsTypeSystemUsageData(classNode)

		["fields": fields, "methods" : methods]
	}
}
