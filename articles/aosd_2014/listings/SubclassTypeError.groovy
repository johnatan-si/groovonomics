class MyRunnable implements Runnable {}

/*
ERROR MESSAGE

$ groovyc SubclassTypeError.groovy
org.codehaus.groovy.control.MultipleCompilationErrorsException: startup failed:
SubclassTypeError.groovy: 1: Can't have an abstract method in a non-abstract class. The class 'MyRunnable' must be declared abstract or the method 'void run()' must be implemented.
 @ line 1, column 1.
   class MyRunnable implements Runnable {}
   ^

1 error
*/
