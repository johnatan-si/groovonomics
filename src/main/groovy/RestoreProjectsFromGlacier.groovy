import org.apache.log4j.Level
import org.apache.log4j.LogManager
import org.apache.log4j.Logger

import com.amazonaws.auth.PropertiesCredentials
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.RestoreObjectRequest


class RestoreProjectsFromGlacier {

	public static void main(args) {
		new RestoreProjectsFromGlacier().go()
	}
	
	def go() {
		def credentials = new PropertiesCredentials(new File("/opt/groovonomics/conf/aws.properties"))
		AmazonS3Client s3 = new AmazonS3Client(credentials)
		
		def projectsListFile = new File("/opt/groovonomics/dataset/projects/list.txt")
		
		def logFile = new File("log.txt")
		logFile.delete()
		
		def frozenProjects = []
		def count = 0
		
		["Chinouchi_AlloSerie"].each{ projectId ->
			
			logFile << "${count++} $projectId\n"
			
			def attempts = 0
			
			while(attempts < 3) {
				try {
					def restoreReq = new RestoreObjectRequest("carlosgsouza.groovonomics", "dataset/projects/source/${projectId}.zip", 10)
					s3.restoreObject(restoreReq)
					
					break
				} catch(e) {
					e.printStackTrace()
				
					logFile <<  "Couldn't restore project $projectId --> $e.message\n"
					attempts++
					
					if(attempts >= 3) {
						frozenProjects.add projectId
					}
				}
			}
		}
		
		if(frozenProjects) {
			logFile <<  "The following projects couldn't be restored\n"
			logFile <<  frozenProjects.join("\n")
		}
	}
	
	static turnOffAnnoyingLogs() {
		List<Logger> loggers = Collections.<Logger>list(LogManager.getCurrentLoggers());
		loggers.add(LogManager.getRootLogger());
		for ( Logger logger : loggers ) {
			logger.setLevel(Level.OFF);
		}
	}
}