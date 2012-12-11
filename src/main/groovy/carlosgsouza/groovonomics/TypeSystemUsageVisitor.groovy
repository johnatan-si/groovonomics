package carlosgsouza.groovonomics

import org.codehaus.groovy.ast.CodeVisitorSupport
import org.codehaus.groovy.ast.expr.ClosureExpression
import org.codehaus.groovy.control.CompilationUnit
import org.codehaus.groovy.control.CompilePhase
import org.codehaus.groovy.control.CompilerConfiguration
import org.codehaus.groovy.control.SourceUnit

class TypeSystemUsageVisitor extends CodeVisitorSupport {

	def file
	
	def TypeSystemUsageVisitor(file) {
		this.file = file
	}
	
	@Override
	public void visitClosureExpression(ClosureExpression closureExpression) {
		super.visitClosureExpression(closureExpression)
		println "visitin a closure"
	}

	@Override
	protected SourceUnit getSourceUnit() {
		// I don't know how this I should implement this, but it make no difference
		return null;
	}
	
	def getTypeSystemUsageData() {
		def sourceUnit = compile(file)
		sourceUnit.ast.visit(this)
	}
	
	def compile(file) {
		def codeSource = new GroovyCodeSource(file)
		def sourceName = "anySourceNameWillDo"
		def classLoader = new GroovyClassLoader(getClass().classLoader)
		
		CompilationUnit cu = new CompilationUnit(CompilerConfiguration.DEFAULT, codeSource.codeSource, classLoader)
		cu.setClassgenCallback(classLoader.createCollector(cu, null))
		cu.addSource(sourceName, file.text)
		cu.compile(CompilePhase.CONVERSION.phaseNumber)
		
		
		cu.sources[sourceName]
	}
 
}
