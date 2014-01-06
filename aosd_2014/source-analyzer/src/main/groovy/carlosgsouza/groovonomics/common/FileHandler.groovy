package carlosgsouza.groovonomics.common

class FileHandler {
	
	File baseDir
	
	def logFile
	def outputFile
	
	public FileHandler(baseDirPath, dirName) {
		this("$baseDirPath/$dirName")
	}
	
	public FileHandler(String baseDirPath) {
		this(new File(baseDirPath))
	}
		
	public FileHandler(File baseDir) {	
		if(!baseDir.exists()) {
			baseDir.mkdir()
		}
		
		this.logFile = new File(baseDir, "log.txt")
		this.outputFile = new File(baseDir, "output.txt")
		
		this.logFile.createNewFile()
		this.outputFile.createNewFile()
	}
	
	def log(String msg) {
		logFile << "INFO\t$msg\n"
		println msg
	}
	
	def logError(String msg) {
		logFile << "ERROR\t$msg\n"
		println msg
	}
	
	def output(msg) {
		outputFile << "$msg\n"
		println msg
	}
	
}
