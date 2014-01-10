package carlosgsouza.groovonomics.typing_usage
import java.text.SimpleDateFormat

import org.apache.commons.lang.exception.ExceptionUtils

import com.amazonaws.auth.PropertiesCredentials
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.GetObjectRequest
import com.amazonaws.services.s3.transfer.TransferManager
import com.amazonaws.services.sns.AmazonSNSClient
import com.amazonaws.services.sns.model.PublishRequest
import com.amazonaws.services.sqs.AmazonSQSClient
import com.amazonaws.services.sqs.model.DeleteMessageRequest
import com.amazonaws.services.sqs.model.Message
import com.amazonaws.services.sqs.model.ReceiveMessageRequest

class AnalyzeTypingUsage {
	
	String localhostname = java.net.InetAddress.getLocalHost().getHostName()
	def id
	def tempDir
	
	def sqs = new SQS()
	def sns = new SNS()
	S3 s3  = new S3()
	FileSystem fs  = new FileSystem()
	
	public AnalyzeTypingUsage(id) {
		this.id = id
		
		localhostname = java.net.InetAddress.getLocalHost().getHostName() + "-$id" 
		tempDir = "/opt/groovonomics/temp_$id"
		
		fs.tempDirPath = tempDir
		s3.tempDirPath = tempDir
	}
	
	
	public static void main(args) {
		def threads = []
		
		3.times { id ->
			threads << Thread.start {
				new AnalyzeTypingUsage(id).justDoIt()
			}
		}
		
		threads*.join()
	}
	
	def justDoIt() {
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
		
		def typeData = new ProjectInspector(new File("$tempDir/$projectId/")).getTypeSystemUsageData()
		
		def w = new File("$tempDir/${projectId}.json").newWriter()
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
		def tempDirPath
		
		def cleanWorkDir() {
			def workDir = new File(tempDirPath)
			workDir.deleteDir()
			workDir.mkdirs()
		}
		
		def unzipSource(projectId) {
			exec "$SCRIPT_DIR/download_and_extract_project.sh $projectId $tempDirPath"
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
		def tempDirPath
		
		AmazonS3Client s3 = new AmazonS3Client(credentials)
		TransferManager transferManager = new TransferManager(credentials) 
		
		def uploadResult(projectId) {
			s3.putObject("carlosgsouza.groovonomics", "data/type_usage/class/${projectId}.json", new File("$tempDirPath/${projectId}.json"))
		}
		
		def downloadSource(projectId) {
			def req = new GetObjectRequest("carlosgsouza.groovonomics", "dataset/projects/source/${projectId}.zip")
			def download = transferManager.download(req, new File("$tempDirPath/${projectId}.zip"))
			download.waitForCompletion()
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
}