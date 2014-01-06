def output = new File('/opt/groovonomics/dataset/projects/list_new.txt')
def input = new File('/opt/groovonomics/dataset/projects/list.txt')

input.eachLine { pid ->
	output << pid.replace('/', '_')
	output << "\n"
}

