import org.apache.log4j.Level
import org.apache.log4j.LogManager
import org.apache.log4j.Logger

import carlosgsouza.groovonomics.typing_usage.ProjectInspector

import com.amazonaws.auth.PropertiesCredentials
import com.amazonaws.services.sqs.AmazonSQSClient
import com.amazonaws.services.sqs.model.DeleteMessageRequest
import com.amazonaws.services.sqs.model.Message
import com.amazonaws.services.sqs.model.ReceiveMessageRequest

def SCRIPT_DIR = "/opt/groovonomics/src/scripts/carlosgsouza/groovonomics/typing_usage_2"
def TEMP_DIR = "/opt/groovonomics/temp"

turnOffAnnoyingLogs()

def sqs = new SQS()

Message msg = sqs.nextMessage
def projectId = msg.body

println "STARTED TO PROCESS PROJECT $projectId"

exec "$SCRIPT_DIR/download_and_extract_project.sh $projectId"

def typeData = new ProjectInspector(new File("$TEMP_DIR/$projectId/")).getTypeSystemUsageData()

def w = new File("$TEMP_DIR/${projectId}.json").newWriter()
w << typeData
w.close()

exec "$SCRIPT_DIR/upload_results.sh"

sqs.deleteMessage(msg)
 
println "FINISHED TO PROCESS PROJECT $projectId"

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
		new DeleteMessageRequest(queueUrl, msg.receiptHandle)
	}
}


def turnOffAnnoyingLogs() {
	List<Logger> loggers = Collections.<Logger>list(LogManager.getCurrentLoggers());
	loggers.add(LogManager.getRootLogger());
	for ( Logger logger : loggers ) {
		logger.setLevel(Level.OFF);
	}
}

def exec(cmd) {
	def proc = cmd.execute()
	proc.consumeProcessOutput(System.out, System.err)
	proc.waitForOrKill(3600000)
}
