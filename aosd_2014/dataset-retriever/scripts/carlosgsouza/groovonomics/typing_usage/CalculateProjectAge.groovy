import groovy.json.JsonSlurper

def ageFile = new File("/Users/carlosgsouza/groovonomics/data/age.json")
def projects = new JsonSlurper().parseText(ageFile.text)

def totalAge = 0 
projects.each { p->
	totalAge += p.age
}

println "$totalAge days = ${totalAge/365} years"