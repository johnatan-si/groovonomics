import groovy.transform.TypeChecked

@TypeChecked
class TypeCheckedGroovyClass {
	
	static int sum(int a, int b) {
		a + b
	}

	public static void main(String[] args) {
		println sum("1", "2")
	}
}

/*
olga:listings carlosgsouza$ groovyc TypeCheckedGroovyClass.groovy
org.codehaus.groovy.control.MultipleCompilationErrorsException: startup failed:
TypeCheckedGroovyClass.groovy: 11: [Static type checking] - Cannot find matching method TypeCheckedGroovyClass#sum(java.lang.String, java.lang.String). Please check if the declared type is right and if the method exists.
 @ line 11, column 11.
   		println sum("1", "2")
             ^

1 error
*/