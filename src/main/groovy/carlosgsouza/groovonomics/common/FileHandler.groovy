package carlosgsouza.groovonomics.common

class FileHandler {
	
	File baseDir
	
	def defaultOutputFile = null
	
	public FileHandler(baseDirPath) {
		this.baseDir = new File(baseDirPath)
	}
	
	def log(String msg) {
		new File(baseDir, "log.txt") << "$msg\n"		
	}
	
	def logError(String msg) {
		new File(baseDir, "error.txt") << "$msg\n"
	}
	
	def output(msg) {
		if(!defaultOutputFile) {
			defaultOutputFile = new File(baseDir, "output.txt")
		}
		
		defaultOutputFile << "$msg\n"
		println msg
	}
	
}
