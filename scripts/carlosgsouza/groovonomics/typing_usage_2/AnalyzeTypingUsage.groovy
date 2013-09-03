import groovy.transform.Field;

import java.text.SimpleDateFormat

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.log4j.Level
import org.apache.log4j.LogManager
import org.apache.log4j.Logger

import carlosgsouza.groovonomics.typing_usage.ProjectInspector

import com.amazonaws.auth.PropertiesCredentials
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.sns.AmazonSNSClient
import com.amazonaws.services.sns.model.PublishRequest
import com.amazonaws.services.sqs.AmazonSQSClient
import com.amazonaws.services.sqs.model.DeleteMessageRequest
import com.amazonaws.services.sqs.model.Message
import com.amazonaws.services.sqs.model.ReceiveMessageRequest

class AnalyzeTypingUsage {
	
	String localhostname = java.net.InetAddress.getLocalHost().getHostName()
	
	def TEMP_DIR = "/opt/groovonomics/temp"
	
	def sqs = new SQS()
	def sns = new SNS()
	def s3  = new S3()
	def fs  = new FileSystem()
	
	
	public static void main(args) {
		new AnalyzeTypingUsage().justDoIt()
	}
	
	def justDoIt() {
		turnOffAnnoyingLogs()
		
		def projectId
		def processedProjects = []
		def projectsCount = 0
		def startTime = System.currentTimeMillis()
		
		sns.log("INFO", "$localhostname started", "")
		
		try {
			Message msg = sqs.nextMessage
			projectId = msg?.body
			
			while(projectId) {
				try {
					println "STARTED TO PROCESS PROJECT $projectId (total=$projectsCount)"
					process projectId
					
					sqs.deleteMessage(msg)
					
					projectsCount++
					processedProjects << projectId
					
					println "FINISHED TO PROCESS PROJECT $projectId  (total=$projectsCount)\n\n"
				} catch(e) {
					handleError(e, projectId)
				} finally {
					msg = sqs.nextMessage
					projectId = msg?.body
				}
			}	
		} catch(e) {
			handleError(e, projectId)
		} finally {
			def totalTime = (System.currentTimeMillis() - startTime) / (60 * 1000)
		
			sns.log("INFO", "$localhostname processed $projectsCount projects", "$localhostname processed $projectsCount projects in $totalTime minutes\n\n" + processedProjects.join("\n"))
		}
	}
	
	def process(projectId) {
		fs.cleanWorkDir()
		s3.downloadSource(projectId)
		fs.unzipSource(projectId)
		
		def typeData = new ProjectInspector(new File("$TEMP_DIR/$projectId/")).getTypeSystemUsageData()
		
		def w = new File("$TEMP_DIR/${projectId}.json").newWriter()
		w << typeData
		w.close()
		
		s3.uploadResult(projectId)
	}
	
	def handleError(e, projectId) {
		sns.log("ERROR", projectId, ExceptionUtils.getStackTrace(e))
		e.printStackTrace()
	}
	
	class FileSystem {
		
		def SCRIPT_DIR = "/opt/groovonomics/src/scripts/carlosgsouza/groovonomics/typing_usage_2"
		
		def cleanWorkDir() {
			def workDir = new File("/opt/groovonomics/temp")
			workDir.deleteDir()
			workDir.mkdirs()
		}
		
		def unzipSource(projectId) {
			exec "$SCRIPT_DIR/download_and_extract_project.sh $projectId"
		}
		
		
		def exec(cmd) {
			def proc = cmd.execute()
			proc.consumeProcessOutput(System.out, System.err)
			proc.waitForOrKill(3600000)
		}
		
	}
	
	class S3 {
		def credentials = new PropertiesCredentials(new File("/opt/groovonomics/conf/aws.properties"))
		def scriptStart = new Date()
		
		AmazonS3Client s3 = new AmazonS3Client(credentials)
		
		def uploadResult(projectId) {
			s3.putObject("carlosgsouza.groovonomics", "data/type_usage/class/${projectId}.json", new File("/opt/groovonomics/temp/${projectId}.json"))
		}
		
		def downloadSource(projectId) {
			def object = s3.getObject("carlosgsouza.groovonomics", "dataset/projects/source/${projectId}.zip")
			
			def w = new File("/opt/groovonomics/temp/${projectId}.zip").newWriter()
			w << object.objectContent
			w.close()
		}
	}
	
	class SQS {
		def credentials = new PropertiesCredentials(new File("/opt/groovonomics/conf/aws.properties"))
		def queueUrl = "https://sqs.us-east-1.amazonaws.com/525294860386/groovonomics-projects"
		
		AmazonSQSClient sqs = new AmazonSQSClient(credentials)
		
		def getNextMessage() {
			def recvMsgReq = new ReceiveMessageRequest(queueUrl)
			def recvMsgResult = sqs.receiveMessage(recvMsgReq)
			def msgs = recvMsgResult.messages
			
			return msgs ? msgs.first() : null
		}
		
		def deleteMessage(Message msg) {
			def deleteReq = new DeleteMessageRequest(queueUrl, msg.receiptHandle)
			sqs.deleteMessage(deleteReq)
		}
	}
	
	class SNS {
		def credentials = new PropertiesCredentials(new File("/opt/groovonomics/conf/aws.properties"))
		def topicArn = "arn:aws:sns:us-east-1:525294860386:groovonomics-log"
		
		def localhostname = java.net.InetAddress.getLocalHost().getHostName()
		
		def sdf = new SimpleDateFormat()
		
		AmazonSNSClient sns = new AmazonSNSClient(credentials)
		
		def now() {
			sdf.format(new Date())
		}
		
		def log(type, projectId, message) {
			def subject = "$type | ${now()} | " + (projectId ?: "unknown project")
			
			def publishRequest = new PublishRequest(topicArn, "($localhostname | ${now()})\n\n$message", subject)
			
			sns.publish(publishRequest)
		}
	}
	
	
	def turnOffAnnoyingLogs() {
		List<Logger> loggers = Collections.<Logger>list(LogManager.getCurrentLoggers());
		loggers.add(LogManager.getRootLogger());
		for ( Logger logger : loggers ) {
			logger.setLevel(Level.OFF);
		}
	}
}