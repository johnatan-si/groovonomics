package carlosgsouza.groovonomics

import org.codehaus.groovy.ast.ASTNode
import org.codehaus.groovy.ast.ClassCodeVisitorSupport
import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.CodeVisitorSupport
import org.codehaus.groovy.ast.ConstructorNode
import org.codehaus.groovy.ast.FieldNode
import org.codehaus.groovy.ast.MethodNode
import org.codehaus.groovy.ast.builder.AstBuilder
import org.codehaus.groovy.ast.expr.ClosureExpression
import org.codehaus.groovy.ast.expr.DeclarationExpression
import org.codehaus.groovy.control.CompilePhase
import org.codehaus.groovy.control.SourceUnit

class ASTInspector {

	def sourceFile
	def classData

	public ASTInspector(sourceFile) {
		this.sourceFile = sourceFile
		this.classData = new ClassData()
	}

	def getFieldsTypeSystemUsageData(fieldNode) {		
		def access = getConfuseAccessModifier(fieldNode)
		
		def fieldData = null
		if(access == AccessModifier.PUBLIC) {
			fieldData = classData.publicField
		} else if(access == AccessModifier.PRIVATE) {
			fieldData = classData.privateField
		} else if(access == AccessModifier.PROTECTED) {
			fieldData = classData.protectedField
		} 
	
		if(fieldNode.isDynamicTyped()) {
			fieldData.d++
		} else {
			fieldData.s++
		}
	}
	

	def getMethodsTypeSystemUsageData(methodNode) {
		def access = getSimpleAccessModifier(methodNode)
		
		def methodReturnData = null
		
		if(access == AccessModifier.PUBLIC) {
			methodReturnData = classData.publicMethodReturn
		} else if(access == AccessModifier.PRIVATE) {
			methodReturnData = classData.privateMethodReturn
		} else if(access == AccessModifier.PROTECTED) {
			methodReturnData = classData.protectedMethodReturn
		}
	
		if(methodNode.isDynamicReturnType()) {
			methodReturnData.d++
		} else {
			methodReturnData.s++
		}
		
		getMethodParametersTypeSystemUsageData(methodNode, access)
		
		methodNode.visit(localVariableVistor)
	}

	def getConstructorsTypeSystemUsageData(constructorNode) {		
		def access = getSimpleAccessModifier(constructorNode)
		
		def methodData = null
		if(access == AccessModifier.PUBLIC) {
			classData.numberOfPublicConstructors++
		} else if(access == AccessModifier.PRIVATE) {
			classData.numberOfPrivateConstructors++
		} else if(access == AccessModifier.PROTECTED) {
			classData.numberOfProtectedConstructors++
		}
	
		getConstructorParametersTypeSystemUsageData(constructorNode, access)
	}

	def getMethodParametersTypeSystemUsageData(methodNode, access) {
		def dynamicTypeParameters = methodNode.parameters.findAll{ it.isDynamicTyped() }.size()
		def staticTypeParameters = methodNode.parameters.findAll{ !it.isDynamicTyped() }.size()
		
		def methodDeclarationTypeCounter = null
		def pureTypeMethodDeclarationTypeCounter = null
		
		if(access == AccessModifier.PUBLIC) {
			methodDeclarationTypeCounter = classData.publicMethodParameter
			pureTypeMethodDeclarationTypeCounter = classData.pureTypeSystemPublicMethods
		} else if(access == AccessModifier.PRIVATE) {
			methodDeclarationTypeCounter = classData.privateMethodParameter
			pureTypeMethodDeclarationTypeCounter = classData.pureTypeSystemPrivateMethods
		} else if(access == AccessModifier.PROTECTED) {
			methodDeclarationTypeCounter = classData.protectedMethodParameter
			pureTypeMethodDeclarationTypeCounter = classData.pureTypeSystemProtectedMethods
		} else {
			// TODO log exception
		}
		
		methodDeclarationTypeCounter.s += staticTypeParameters 
		methodDeclarationTypeCounter.d += dynamicTypeParameters
		
		def hasOnlyDynamicTypeParameters = dynamicTypeParameters > 0 && staticTypeParameters == 0
		def hasOnlyeStaticTypeParameters = dynamicTypeParameters == 0 && staticTypeParameters > 0
		
		if(methodNode.isDynamicReturnType() && hasOnlyDynamicTypeParameters) {
			pureTypeMethodDeclarationTypeCounter.d++
		} else if(!methodNode.isDynamicReturnType() && hasOnlyeStaticTypeParameters) {
			pureTypeMethodDeclarationTypeCounter.s++
		}
		
	}

	def getConstructorParametersTypeSystemUsageData(methodNode, access) {
		def dynamicTypeParameters = methodNode.parameters.findAll{ it.isDynamicTyped() }.size()
		def staticTypeParameters = methodNode.parameters.findAll{ !it.isDynamicTyped() }.size()
		
		def constructorDeclarationTypeCounter = null
		def pureTypeConstructorDeclarationTypeCounter = null
		
		if(access == AccessModifier.PUBLIC) {
			constructorDeclarationTypeCounter = classData.publicConstructorParameter
			pureTypeConstructorDeclarationTypeCounter = classData.pureTypeSystemPublicConstructors
		} else if(access == AccessModifier.PRIVATE) {
			constructorDeclarationTypeCounter = classData.privateConstructorParameter
			pureTypeConstructorDeclarationTypeCounter = classData.pureTypeSystemPrivateConstructors
		} else if(access == AccessModifier.PROTECTED) {
			constructorDeclarationTypeCounter = classData.protectedConstructorParameter
			pureTypeConstructorDeclarationTypeCounter = classData.pureTypeSystemProtectedConstructors
		} else {
			// TODO log exception
		}
		
		constructorDeclarationTypeCounter.s += staticTypeParameters
		constructorDeclarationTypeCounter.d += dynamicTypeParameters
		
		def hasOnlyDynamicTypeParameters = dynamicTypeParameters > 0 && staticTypeParameters == 0
		def hasOnlyeStaticTypeParameters = dynamicTypeParameters == 0 && staticTypeParameters > 0
		
		if(hasOnlyDynamicTypeParameters) {
			pureTypeConstructorDeclarationTypeCounter.d++
		} else if(hasOnlyeStaticTypeParameters) {
			pureTypeConstructorDeclarationTypeCounter.s++
		}
		
	}
	
	def getTypeSystemUsageData() {
		def classCodeVisitor = new ClassCodeVisitor()
		
		List<ASTNode> nodes = new AstBuilder().buildFromString(CompilePhase.CONVERSION, false, sourceFile.text)
		def classNode = nodes.find { it.class == ClassNode.class }
		
		classCodeVisitor.visitClass(classNode)
		
		classData
	}
	
	def getConfuseAccessModifier(node) {
		if(node.isPublic() && !node.isProtected()) {
			AccessModifier.PUBLIC
		} else if(!node.isPublic() && node.isProtected()) {
			AccessModifier.PROTECTED
		} else if(!node.isPublic() && !node.isProtected()) {
			AccessModifier.PRIVATE
		} else {
			// TODO log error
		}
	}
	
	def getSimpleAccessModifier(node) {
		if(node.isPublic()) {
			AccessModifier.PUBLIC
		} else if(node.isProtected()) {
			AccessModifier.PROTECTED
		} else if(node.isPrivate()) {
			AccessModifier.PRIVATE
		} else {
			// TODO log this
		}
	}
	
	enum AccessModifier {
		PUBLIC, PRIVATE, PROTECTED
	}
	
	enum DeclarationType {
		STATIC, DYNAMIC
	}
	
	class LocalVariableDeclarationVisitor extends CodeVisitorSupport {

		@Override
		public void visitDeclarationExpression(DeclarationExpression expression) {
			if(expression.variableExpression.isDynamicTyped()) {
				counter.d++
			} else {
				counter.s++
			}
			
			super.visitDeclarationExpression(expression)
		}
		
		public void visitClosureExpression(ClosureExpression closureExpression) {
			// once we get to a closure we don't get any local variable declaration anymore
			return
		}
		
		@Override
		protected SourceUnit getSourceUnit() {
			return null // should I do anything here?
		}

	}
	
	class ClassCodeVisitor extends ClassCodeVisitorSupport {
		
		public ClassCodeVisitor() {
		}
		
		@Override
		public void visitField(FieldNode node) {
			getFieldsTypeSystemUsageData(node)
			super.visitField(node)
		}
		
		@Override
		public void visitMethod(MethodNode node) {
			getMethodsTypeSystemUsageData(node)
	        super.visitMethod(node)
	    }
		
		@Override
		public void visitConstructor(ConstructorNode node) {
			getConstructorsTypeSystemUsageData(node)
			super.visitConstructor(node)
		}

		@Override
		protected SourceUnit getSourceUnit() {
			return null // should I do anything here?
		}
	}
}

