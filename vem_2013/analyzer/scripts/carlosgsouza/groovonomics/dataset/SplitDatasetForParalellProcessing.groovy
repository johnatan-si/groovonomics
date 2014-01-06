/*
 * Splits the dataset in smaller pieces such that each piece can be processed by a different instance
 */

def baseFolder = new File("/opt/groovonomics/dataset/list/")
def projectsFile = new File(baseFolder, "output.txt")
def totalInstances = 19
def files = []

totalInstances.times {
	files[it] = new File(baseFolder, "${it}.txt")
}

def i = 0
projectsFile.eachLine {
	files[i%totalInstances] << it
	files[i%totalInstances] << "\n"
	i++
}
